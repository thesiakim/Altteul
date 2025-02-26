package com.c203.altteulbe.game.service.side;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.c203.altteulbe.room.persistent.entity.Room;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.item.Item;
import com.c203.altteulbe.game.persistent.entity.item.ItemHistory;
import com.c203.altteulbe.game.persistent.entity.side.SideProblem;
import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.persistent.repository.item.ItemHistoryRepository;
import com.c203.altteulbe.game.persistent.repository.item.ItemRepository;
import com.c203.altteulbe.game.persistent.repository.side.SideProblemHistoryRepository;
import com.c203.altteulbe.game.persistent.repository.side.SideProblemRepository;
import com.c203.altteulbe.game.service.exception.GameNotFoundException;
import com.c203.altteulbe.game.service.exception.ItemNotFoundException;
import com.c203.altteulbe.game.service.exception.ProblemNotFoundException;
import com.c203.altteulbe.game.service.exception.SideProblemNotFoundException;
import com.c203.altteulbe.game.web.dto.side.request.ReceiveSideProblemRequestDto;
import com.c203.altteulbe.game.web.dto.side.request.SubmitSideProblemRequestDto;
import com.c203.altteulbe.game.web.dto.side.response.ReceiveSideProblemResponseDto;
import com.c203.altteulbe.game.web.dto.side.response.SubmitSideProblemResponseDto;
import com.c203.altteulbe.room.persistent.entity.SingleRoom;
import com.c203.altteulbe.room.persistent.entity.TeamRoom;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRepository;
import com.c203.altteulbe.room.persistent.repository.team.TeamRoomRepository;
import com.c203.altteulbe.room.service.exception.RoomNotFoundException;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.service.exception.NotFoundUserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SideProblemService {

	private final SideProblemRepository sideProblemRepository;
	private final SideProblemHistoryRepository sideProblemHistoryRepository;
	private final TeamRoomRepository teamRoomRepository;
	private final SideProblemWebsocketService sideProblemWebsocketService;
	private final GameRepository gameRepository;
	private final SingleRoomRepository singleRoomRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final ItemHistoryRepository itemHistoryRepository;

	public void submit(SubmitSideProblemRequestDto message, Long id) {
		// 제출된 결과 확인
		SideProblem sideProblem = sideProblemRepository.findById(message.getSideProblemId())
			.orElseThrow(SideProblemNotFoundException::new);

		SideProblemHistory.ProblemResult result =
			message.getAnswer().equals(sideProblem.getAnswer()) ? SideProblemHistory.ProblemResult.P :
				SideProblemHistory.ProblemResult.F;

		Game game = gameRepository.findWithRoomByGameId(message.getGameId())
			.orElseThrow(GameNotFoundException::new);
		Room room;
		if (game.getBattleType() == BattleType.S) {
			room = singleRoomRepository.findById(message.getTeamId()).orElseThrow(RoomNotFoundException::new);
		} else {
			room = teamRoomRepository.findById(message.getTeamId()).orElseThrow(RoomNotFoundException::new);
		}

		// 채점 결과 브로드 캐스트
		if (result == SideProblemHistory.ProblemResult.P) {
			if (game.getBattleType() == BattleType.S) {
				// 개인전
				sideProblemWebsocketService.sendSubmissionResult(
					SubmitSideProblemResponseDto.builder()
						.roomId(room instanceof SingleRoom ? ((SingleRoom) room).getId() : null)
						.status(result)
						.bonusPoint(50)
						.build(),
					message.getGameId(),
					message.getTeamId()
				);
			} else {
				// 팀전

				// 아이템 획득
				Random random = new Random();
				long totalCount = itemRepository.count();
				long randomId = random.nextLong(totalCount) + 1;
				Item item = itemRepository.findById(randomId)
					.orElseThrow(ItemNotFoundException::new);

				// 결과 브로드 캐스트
				sideProblemWebsocketService.sendSubmissionResult(
					SubmitSideProblemResponseDto.builder()
						.roomId(room instanceof TeamRoom ? ((TeamRoom) room).getId() : null)
						.status(result)
						.itemId(item.getId())
						.itemName(item.getItemName())
						.build(),
					message.getGameId(),
					message.getTeamId()
				);

				// 아이템 획득 내역 저장
				itemHistoryRepository.save(
					ItemHistory.builder()
						.item(item)
						.game(game)
						.userId(id)
						.teamRoom(message.getTeamId())
						.type(ItemHistory.Type.H)
						.build()
				);
			}
		} else {
			sideProblemWebsocketService.sendSubmissionResult(
				SubmitSideProblemResponseDto.builder()
					.status(result)
					.build(),
				message.getGameId(),
				message.getTeamId()
			);
		}

		// 결과를 사이드 문제 풀이내역에 저장

		SideProblemHistory sideProblemHistory = null;
		User user = userRepository.findByUserId(id)
			.orElseThrow(NotFoundUserException::new);

		if (game.getBattleType() == BattleType.S) {
			sideProblemHistory = SideProblemHistory.builder()
				.sideProblemId(sideProblem)
				.gameId(game)
				.result(result)
				.teamRoomId(null)
				.userId(user)
				.userAnswer(message.getAnswer())
				.build();
		} else {
			TeamRoom teamRoom = teamRoomRepository.findById(message.getTeamId())
				.orElseThrow(RoomNotFoundException::new);

			sideProblemHistory = SideProblemHistory.builder()
				.sideProblemId(sideProblem)
				.gameId(game)
				.result(result)
				.teamRoomId(teamRoom)
				.userId(user)
				.userAnswer(message.getAnswer())
				.build();
		}
		sideProblemHistoryRepository.save(sideProblemHistory);
	}

	public void receive(ReceiveSideProblemRequestDto message, Long id) {
		// 팀의 문제를 구독하는 사람에게 문제 전달
		long totalCount = sideProblemRepository.count();

		// 게임 찾기
		Game game = gameRepository.findWithRoomByGameId(message.getGameId())
			.orElseThrow(GameNotFoundException::new);

		List<SideProblemHistory> histories = null;

		if (game.getBattleType() == BattleType.S) {
			SingleRoom room = singleRoomRepository.findById(message.getTeamId())
				.orElseThrow(RoomNotFoundException::new);
			histories = sideProblemHistoryRepository.findByUserId(room.getUser());
		} else {
			TeamRoom room = teamRoomRepository.findById(message.getTeamId())
				.orElseThrow(RoomNotFoundException::new);
			histories = sideProblemHistoryRepository.findByTeamRoomId(room);
		}

		// 이전에 풀었던 문제 추출
		Set<Long> solved = new HashSet<>();
		for (SideProblemHistory history : histories) {
			solved.add(history.getSideProblemId().getId());
		}

		Random random = new Random();

		// 랜덤 번호와 안겹칠때까지 찾고 추출
		SideProblem nextProblem = null;

		while (solved.size() < 30) {
			long randomId = random.nextLong(totalCount) + 1; // 1부터 totalCount까지의 랜덤 숫자
			if (!solved.contains(randomId)) {
				nextProblem = sideProblemRepository.findById(randomId)
					.orElseThrow(ProblemNotFoundException::new);
				break;
			}
		}

		if (nextProblem == null) {
			throw new NullPointerException();
		}

		sideProblemWebsocketService.sendSideProblem(
			ReceiveSideProblemResponseDto.builder()
				.id(nextProblem.getId())
				.title(nextProblem.getTitle())
				.description(nextProblem.getContent())
				.build(),
			message.getGameId(),
			message.getTeamId()
		);
	}
}
