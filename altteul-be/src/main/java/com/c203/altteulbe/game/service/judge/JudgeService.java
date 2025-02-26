package com.c203.altteulbe.game.service.judge;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.c203.altteulbe.common.dto.BattleResult;
import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.dto.Language;
import com.c203.altteulbe.common.dto.PointType;
import com.c203.altteulbe.common.exception.BusinessException;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.PointHistory;
import com.c203.altteulbe.game.persistent.entity.TestHistory;
import com.c203.altteulbe.game.persistent.entity.TestResult;
import com.c203.altteulbe.game.persistent.entity.problem.Problem;
import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.persistent.repository.history.TestHistoryRepository;
import com.c203.altteulbe.game.persistent.repository.problem.ProblemRepository;
import com.c203.altteulbe.game.persistent.repository.side.SideProblemHistoryRepository;
import com.c203.altteulbe.game.service.PointHistoryService;
import com.c203.altteulbe.game.web.dto.judge.request.JudgeRequestDto;
import com.c203.altteulbe.game.web.dto.judge.request.SubmitCodeRequestDto;
import com.c203.altteulbe.game.web.dto.judge.request.lang.LangDto;
import com.c203.altteulbe.game.web.dto.judge.request.lang.LangDtoFactory;
import com.c203.altteulbe.game.web.dto.judge.response.CodeExecutionResponseDto;
import com.c203.altteulbe.game.web.dto.judge.response.CodeSubmissionOpponentResponseDto;
import com.c203.altteulbe.game.web.dto.judge.response.CodeSubmissionTeamResponseDto;
import com.c203.altteulbe.game.web.dto.judge.response.JudgeResponse;
import com.c203.altteulbe.game.web.dto.judge.response.PingResponse;
import com.c203.altteulbe.game.web.dto.judge.response.TestCaseResponseDto;
import com.c203.altteulbe.room.persistent.entity.Room;
import com.c203.altteulbe.room.persistent.entity.SingleRoom;
import com.c203.altteulbe.room.persistent.entity.TeamRoom;
import com.c203.altteulbe.room.persistent.entity.UserTeamRoom;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRepository;
import com.c203.altteulbe.room.persistent.repository.team.TeamRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JudgeService {
	private final RestTemplate restTemplate;
	private final JudgeWebsocketService judgeWebsocketService;
	private final ProblemRepository problemRepository;
	private final TestHistoryRepository testHistoryRepository;
	private final GameRepository gameRepository;
	private final SingleRoomRepository singleRoomRepository;
	private final TeamRoomRepository teamRoomRepository;
	private final PointHistoryService pointHistoryService;
	private final SideProblemHistoryRepository sideProblemHistoryRepository;

	@Value("${judge.server.url}")
	private String judgeServerUrl;

	private static final String PROBLEM_PREFIX = "problem_";
	private static final String EXAMPLE_PREFIX = "example_";

	// 시스템 정보 조회
	public PingResponse getSystemInfo() {
		String url = judgeServerUrl + "/ping";
		return restTemplate.postForObject(url, null, PingResponse.class);
	}

	// 일반 채점 실행
	public JudgeResponse submitToJudge(SubmitCodeRequestDto request, String prefix, Problem problem) {
		// 채점 서버 url
		String url = judgeServerUrl + "/judge";
		String problemFolderName = prefix + request.getProblemId();

		// 언어에 따른 설정 분리
		LangDto langDto = switch (request.getLang()) {
			case "JV" -> LangDtoFactory.createJavaLangDto();
			case "PY" -> LangDtoFactory.createPython3LangDto();
			case "CPP" -> LangDtoFactory.createCppLangDto();
			case "JS" -> LangDtoFactory.createJSLangDto();
			default -> null;
		};

		JudgeRequestDto judgeRequestDto = JudgeRequestDto.builder()
			.src(request.getCode())
			.language_config(langDto)
			.max_cpu_time(3000L) // 기본 1초
			.max_memory(125 * 1024 * 1024L) // 기본 100MB
			.test_case_id(problemFolderName)
			.output(true)
			.build();

		return restTemplate.postForObject(url, judgeRequestDto, JudgeResponse.class);
	}

	/**
	 * 저지에게 평가받을 결과를 호출하여 메세지를 발송하는 함수
	 * @param request : request dto
	 * @param id : username
	 */
	public void submitCode(SubmitCodeRequestDto request, Long id) {
		log.debug("코드 제출 요청 값: {}", request.toString());
		// 저지에게 코드 제출
		Problem problem = problemRepository.findById(request.getProblemId())
			.orElseThrow(() -> new BusinessException("문제를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		JudgeResponse judgeResponse = submitToJudge(request, PROBLEM_PREFIX, problem);

		if (judgeResponse == null)
			throw new NullPointerException();

		CodeSubmissionTeamResponseDto teamResponseDto = CodeSubmissionTeamResponseDto.from(judgeResponse, id);
		CodeSubmissionOpponentResponseDto opponentResponseDto;
		if (judgeResponse.isNotCompileError()) {
			log.debug("코드 제출 결과 값: {}", judgeResponse);
			opponentResponseDto = CodeSubmissionOpponentResponseDto.builder()
				.userId(id)
				.totalCount(teamResponseDto.getTotalCount())
				.passCount(teamResponseDto.getPassCount())
				.status(teamResponseDto.getStatus())
				.build();

		} else {
			opponentResponseDto = CodeSubmissionOpponentResponseDto.builder()
				.userId(id)
				.passCount(null)
				.totalCount(null)
				.build();
		}

		// 결과를 내역 db에 저장, 게임 db에 저장
		// 1. 내역 Entity 생성, 레포지토리 쿼리문 생성, 언어별 제한 테이블 추가

		int maxMemory = -1;
		int maxExecutionTime = -1;
		if (judgeResponse.isNotCompileError()) {
			for (TestCaseResponseDto testCase : teamResponseDto.getTestCases()) {
				maxMemory = Math.max(maxMemory, Integer.parseInt(testCase.getExecutionMemory()));
				maxExecutionTime = Math.max(maxExecutionTime, Integer.parseInt(testCase.getExecutionTime()));
			}
		}

		TestHistory testHistory = TestHistory.from(
			teamResponseDto,
			request.getGameId(),
			request.getProblemId(),
			id,
			String.valueOf(maxExecutionTime),
			String.valueOf(maxMemory),
			request.getCode(),
			request.getLang()
		);
		Game game = gameRepository.findWithAllMemberByGameId(request.getGameId())
			.orElseThrow(() -> new BusinessException("게임 찾을 수 없음", HttpStatus.NOT_FOUND));

		updateRoomSubmission(game, request.getTeamId(), testHistory, request.getCode(), maxExecutionTime, maxMemory,
			Language.valueOf(request.getLang()));

		List<TestResult> testResults = TestResult.from(judgeResponse, testHistory);
		testHistory.updateTestResults(testResults);
		testHistoryRepository.save(testHistory);

		// 채점 결과 팀별로 메세지 전송
		judgeWebsocketService.sendTeamSubmissionResult(teamResponseDto,
			request.getGameId(),
			request.getTeamId());

		judgeWebsocketService.sendOpponentSubmissionResult(opponentResponseDto,
			request.getGameId(),
			request.getTeamId());

		// 실시간 게임 현황 전송
		judgeWebsocketService.sendSubmissionResult(request.getGameId(), request.getTeamId());
	}

	public CodeExecutionResponseDto executeCode(SubmitCodeRequestDto request, Long userId) {
		Problem problem = problemRepository.findWithExamplesByProblemId(request.getProblemId())
			.orElseThrow(() -> new BusinessException("문제를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST));
		JudgeResponse judgeResponse = submitToJudge(request, EXAMPLE_PREFIX, problem);

		if (judgeResponse == null)
			throw new NullPointerException();

		// request.problemId의 테스트케이스 1,2번 answer 정보가 필요함.
		CodeExecutionResponseDto responseDto = CodeExecutionResponseDto.from(judgeResponse, problem, userId);

		judgeWebsocketService.sendExecutionResult(
			responseDto,
			request.getGameId(),
			request.getTeamId()
		);
		return responseDto;
	}

	// 함수 추출
	private void updateRoomSubmission(Game game, Long teamId, TestHistory testHistory, String code,
		int maxExecutionTime, int maxMemory, Language lang) {
		if (game.getBattleType() == BattleType.S) {
			SingleRoom myRoom = singleRoomRepository.findById(teamId)
				.orElseThrow(() -> new BusinessException("해당 팀의 방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
			myRoom.updateSubmissionRecord(
				testHistory.getSuccessCount(),
				String.valueOf(maxExecutionTime),
				String.valueOf(maxMemory),
				code,
				lang
			);
			// 다 맞춰서 게임 종료할 경우 로직
			if (testHistory.getFailCount() != null && testHistory.getFailCount() == 0) {
				finishGame(game, game.getSingleRooms(), myRoom, testHistory.getSuccessCount());
			}

		} else {
			TeamRoom myRoom = teamRoomRepository.findById(teamId)
				.orElseThrow(() -> new BusinessException("해당 팀의 방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
			myRoom.updateSubmissionRecord(
				testHistory.getSuccessCount(),
				String.valueOf(maxExecutionTime),
				String.valueOf(maxMemory),
				code,
				lang
			);
			// 다 맞춰서 게임 종료할 경우 로직
			if (testHistory.getFailCount() != null && testHistory.getFailCount() == 0) {
				finishGame(game, game.getTeamRooms(), myRoom, testHistory.getSuccessCount());
			}
		}
	}

	private void finishGame(Game game, List<? extends Room> rooms, Room myRoom, int totalTestcaseCount) {
		// 순위 갱신: finishTime 이 있는 방들만 정렬
		int finishedTeamCount = (int)rooms.stream()
			.filter(room -> room.getFinishTime() != null) // finishTime 이 설정된 방만 선택
			.filter(room -> room.getSolvedTestcaseCount() == totalTestcaseCount)
			.count();
		BattleResult result = BattleResult.fromRank(finishedTeamCount + 1); // 순위 틀리는 오류 수정
		myRoom.updateStatusByGameClear(result);

		// FAIL이 아닐 때만 사이드 문제 포인트 추가
		if (result != BattleResult.FAIL && myRoom instanceof SingleRoom) {
			List<SideProblemHistory> solvedSideProblemHistories =
				sideProblemHistoryRepository.findByUserIdAndGameIdAndResult(
					((SingleRoom)myRoom).getUser(),
					game,
					SideProblemHistory.ProblemResult.P
				);
			myRoom.addSideProblemPoint(solvedSideProblemHistories.size());
		}

		// 팀전의 경우 모든 팀의 결과가 나왔으므로 바로 게임 완료 처리
		if (game.getBattleType() == BattleType.T) {
			game.completeGame();
		}

		/**
		 * 배틀 포인트 정산 (여기까지 왔다는 것은 FAIL이 없다는 뜻)
		 * 1. BattleResult - 1 : 승리 포인트 50 + 클리어 포인트 100 + 사이드 문제 풀이 내역 조회 후 포인트 개당 20
		 * 2. BattleResult - 2 ~ 8 : 클리어 포인트 100 + 사이드 문제 풀이 내역 조회 후 포인트 개당 20
		 */
		List<PointHistory> pointHistories = new ArrayList<>();
		if (myRoom.getBattleResult() == BattleResult.FIRST) {
			if (myRoom instanceof SingleRoom) {
				pointHistories.add(
					PointHistory.create(
						game,
						((SingleRoom)myRoom).getUser(),
						null,
						100,
						game.getBattleType(),
						PointType.D
					)
				);
				pointHistories.add(
					PointHistory.create(
						game,
						((SingleRoom)myRoom).getUser(),
						null,
						50,
						game.getBattleType(),
						PointType.B
					)
				);
				List<SideProblemHistory> solvedSideProblemHistories = sideProblemHistoryRepository.findByUserIdAndGameIdAndResult(
					((SingleRoom)myRoom).getUser(), game, SideProblemHistory.ProblemResult.P);

				for (SideProblemHistory sideProblemHistory : solvedSideProblemHistories) {
					pointHistories.add(
						PointHistory.create(
							game,
							((SingleRoom)myRoom).getUser(),
							sideProblemHistory.getSideProblemId(),
							20,
							game.getBattleType(),
							PointType.S,
							sideProblemHistory.getCreatedAt()
						)
					);
				}
			} else if (myRoom instanceof TeamRoom) {
				for (UserTeamRoom userTeamRoom : ((TeamRoom)myRoom).getUserTeamRooms()) {
					pointHistories.add(
						PointHistory.create(
							game,
							userTeamRoom.getUser(),
							null,
							100,
							game.getBattleType(),
							PointType.D
						)
					);
					pointHistories.add(
						PointHistory.create(
							game,
							userTeamRoom.getUser(),
							null,
							50,
							game.getBattleType(),
							PointType.B
						)
					);
				}
			}
		} else if (myRoom.getBattleResult() != BattleResult.FAIL) {
			if (myRoom instanceof SingleRoom) {
				pointHistories.add(
					PointHistory.create(
						game,
						((SingleRoom)myRoom).getUser(),
						null,
						100,
						game.getBattleType(),
						PointType.D
					)
				);
				List<SideProblemHistory> solvedSideProblemHistories = sideProblemHistoryRepository.findByUserIdAndGameIdAndResult(
					((SingleRoom)myRoom).getUser(), game, SideProblemHistory.ProblemResult.P);

				for (SideProblemHistory sideProblemHistory : solvedSideProblemHistories) {
					pointHistories.add(
						PointHistory.create(
							game,
							((SingleRoom)myRoom).getUser(),
							sideProblemHistory.getSideProblemId(),
							20,
							game.getBattleType(),
							PointType.S,
							sideProblemHistory.getCreatedAt()
						)
					);
				}
			} else if (myRoom instanceof TeamRoom) {
				for (UserTeamRoom userTeamRoom : ((TeamRoom)myRoom).getUserTeamRooms()) {
					pointHistories.add(
						PointHistory.create(
							game,
							userTeamRoom.getUser(),
							null,
							100,
							game.getBattleType(),
							PointType.D
						)
					);
				}
			}
		}

		// 포인트 내역 저장
		pointHistoryService.savePointHistory(pointHistories);
	}
}

