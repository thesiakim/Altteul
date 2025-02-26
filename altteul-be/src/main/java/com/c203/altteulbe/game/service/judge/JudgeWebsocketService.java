package com.c203.altteulbe.game.service.judge;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.service.exception.GameNotFoundException;
import com.c203.altteulbe.game.web.dto.judge.response.CodeExecutionResponseDto;
import com.c203.altteulbe.game.web.dto.judge.response.CodeSubmissionOpponentResponseDto;
import com.c203.altteulbe.game.web.dto.judge.response.CodeSubmissionTeamResponseDto;
import com.c203.altteulbe.game.web.dto.result.response.GameResultResponseDto;
import com.c203.altteulbe.game.web.dto.result.response.TeamInfo;
import com.c203.altteulbe.room.service.exception.RoomNotFoundException;
import com.c203.altteulbe.websocket.dto.response.WebSocketResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeWebsocketService {
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final GameRepository gameRepository;

	public void sendTeamSubmissionResult(CodeSubmissionTeamResponseDto codeSubmissionTeamResponseDto,
		Long gameId, Long teamId) {
		// 우리 팀 구독 경로로 결과 전송 (자세한 테스트케이스 결과 포함 or 에러 메세지)
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/team-submission/result",
			WebSocketResponse.withData("팀 제출 결과", codeSubmissionTeamResponseDto));
	}

	public void sendOpponentSubmissionResult(CodeSubmissionOpponentResponseDto codeSubmissionOpponentResponseDto,
		Long gameId, Long teamId) {
		// 상대 팀 구독 경로로 결과 전송 (간략한 정보만 전송)
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/opponent-submission/result",
			WebSocketResponse.withData("상대 팀 제출 결과", codeSubmissionOpponentResponseDto));
	}

	public void sendExecutionResult(CodeExecutionResponseDto codeExecutionResponseDto, Long gameId, Long teamId) {
		// 우리 팀 구독 경로로 결과 전송 (자세한 테스트케이스 결과 포함 or 에러 메세지)
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/execution/result",
			WebSocketResponse.withData("코드 실행 결과", codeExecutionResponseDto));
	}

	public void sendSubmissionResult(Long gameId, Long roomId) {
		Game game = gameRepository.findWithAllMemberByGameId(gameId)
			.orElseThrow(GameNotFoundException::new);

		List<TeamInfo> allTeamInfos = game.getBattleType() == BattleType.S ?
			game.getSingleRooms().stream()
				.map(room -> TeamInfo.fromSingleRoom(room, game.getProblem().getTotalCount()))
				.toList() :
			game.getTeamRooms().stream()
				.map(room -> TeamInfo.fromTeamRoom(room, game.getProblem().getTotalCount()))
				.toList();

		TeamInfo myTeam = allTeamInfos.stream()
			.filter(info -> info.getTeamId().equals(roomId))
			.findFirst()
			.orElseThrow(RoomNotFoundException::new);

		List<TeamInfo> opponents = allTeamInfos.stream()
			.filter(info -> !info.getTeamId().equals(roomId))
			.toList();

		GameResultResponseDto resultDto = GameResultResponseDto.from(game, myTeam, opponents);

		simpMessagingTemplate.convertAndSend(
			"/sub/game/" + gameId + "/submission/result",
			WebSocketResponse.withData("게임 현황", resultDto));
	}

}
