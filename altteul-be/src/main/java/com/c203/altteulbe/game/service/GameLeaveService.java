package com.c203.altteulbe.game.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c203.altteulbe.common.dto.BattleResult;
import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.utils.RedisKeys;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.service.exception.GameNotFoundException;
import com.c203.altteulbe.game.web.dto.leave.request.GameLeaveRequestDto;
import com.c203.altteulbe.game.web.dto.leave.response.SingleGameLeaveResponseDto;
import com.c203.altteulbe.game.web.dto.leave.response.TeamGameLeaveResponseDto;
import com.c203.altteulbe.room.persistent.entity.SingleRoom;
import com.c203.altteulbe.room.persistent.entity.TeamRoom;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRepository;
import com.c203.altteulbe.room.persistent.repository.team.TeamRoomRepository;
import com.c203.altteulbe.room.service.RoomValidator;
import com.c203.altteulbe.room.service.RoomWebSocketService;
import com.c203.altteulbe.room.service.exception.RoomNotFoundException;
import com.c203.altteulbe.room.service.exception.UserNotInRoomException;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.service.exception.NotFoundUserException;
import com.c203.altteulbe.user.web.dto.response.UserInfoResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameLeaveService {
	private final UserRepository userRepository;
	private final RoomValidator roomValidator;
	private final GameRepository gameRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final SingleRoomRepository singleRoomRepository;
	private final TeamRoomRepository teamRoomRepository;
	private final RoomWebSocketService roomWebSocketService;

	private static final String TEAM_LEFT_STATUS = "TEAM_LEFT";

	@Transactional
	public void leaveGame(Long userId, GameLeaveRequestDto request) {

		User user = userRepository.findByUserId(userId)
			.orElseThrow(NotFoundUserException::new);

		// 유저가 속한 방 ID 조회 (Redis에서)
		BattleType battleType;
		if (roomValidator.isUserInAnyRoom(userId, BattleType.S)) {
			battleType = BattleType.S;
		} else if (roomValidator.isUserInAnyRoom(userId, BattleType.T)) {
			battleType = BattleType.T;
			if (request.getRoomId() == null) {
				throw new GameNotFoundException();
			}
		} else {
			throw new UserNotInRoomException();
		}

		// 게임 정보 조회 (이미 구현된 메서드 활용)
		Game game = gameRepository.findWithGameByRoomIdAndType(request.getRoomId(), battleType)
			.orElseThrow(GameNotFoundException::new);
		if (!game.isInProgress()) {
			// 정상 종료된 게임 - 단순 정리 작업
			handleFinishedGameLeave(game, user, request.getRoomId());
		} else {
			// 진행 중인 게임 - 중도 퇴장 처리
			handleInProgressGameLeave(game, user, request.getRoomId());
		}
	}

	// 게임 정상 종료 후 나가기 처리
	private void handleFinishedGameLeave(Game game, User user, Long roomId) {
		if (game.getBattleType() == BattleType.S) {
			handleFinishedSingleGameLeave(game, user, roomId);
		} else {
			handleFinishedTeamGameLeave(game, user, roomId);
		}
	}

	// 게임 중간 퇴장 처리
	private void handleInProgressGameLeave(Game game, User user, Long roomId) {
		if (game.getBattleType() == BattleType.S) {
			handleInProgressSingleGameLeave(game, user, roomId);
		} else {
			handleInProgressTeamGameLeave(game, user, roomId);
		}
	}

	// 팀전 정상 종료 후 나가기 처리
	private void handleFinishedTeamGameLeave(Game game, User user, Long roomId) {
		handleTeamGameLeave(game, user, roomId, "GAME_FINISH_LEAVE", false);
	}

	// 팀전 퇴장 처리
	private void handleInProgressTeamGameLeave(Game game, User user, Long roomId) {
		handleTeamGameLeave(game, user, roomId, "GAME_IN_PROGRESS_LEAVE", true);
	}

	// 개인전 정상 종료 후 나가기 처리
	private void handleFinishedSingleGameLeave(Game game, User user, Long roomId) {
		handleSingleGameLeave(game, user, roomId, "GAME_FINISH_LEAVE", false);
	}

	// 개인전 중간 퇴장 처리
	private void handleInProgressSingleGameLeave(Game game, User user, Long roomId) {
		handleSingleGameLeave(game, user, roomId, "GAME_IN_PROGRESS_LEAVE", true);
	}

	// 남은 유저 정보 조회
	private List<UserInfoResponseDto> getRemainingUsers(List<String> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<User> users = userRepository.findByUserIdIn(
			userIds.stream()
				.map(Long::parseLong)
				.collect(Collectors.toList())
		);

		return users.stream()
			.map(UserInfoResponseDto::fromEntity)
			.collect(Collectors.toList());
	}

	// 팀별 남은 유저 정보 조회
	private Map<Long, List<UserInfoResponseDto>> getRemainingTeamUsers(String redisRoomId, Long leftUserId) {
		Map<Long, List<UserInfoResponseDto>> remainingUsersByTeam = new HashMap<>();

		// matchId를 이용해 양쪽 팀의 roomId를 얻기
		String matchId = redisTemplate.opsForValue().get(RedisKeys.TeamMatchId(Long.parseLong(redisRoomId)));
		String[] roomIds = matchId.split("-");

		for (String roomId : roomIds) {
			Long teamRoomId = Long.parseLong(roomId);
			String roomUsersKey = RedisKeys.TeamRoomUsers(teamRoomId);
			String roomDbId = redisTemplate.opsForValue().get(RedisKeys.getRoomDbId(teamRoomId));
			List<String> userIds = redisTemplate.opsForList().range(roomUsersKey, 0, -1);

			if (userIds != null && !userIds.isEmpty()) {
				List<User> users = userRepository.findByUserIdIn(
					userIds.stream()
						.map(Long::parseLong)
						.filter(id -> !id.equals(leftUserId))
						.collect(Collectors.toList())
				);

				remainingUsersByTeam.put(Long.parseLong(Objects.requireNonNull(roomDbId)),
					users.stream()
						.map(UserInfoResponseDto::fromEntity)
						.collect(Collectors.toList())
				);
			}
		}

		return remainingUsersByTeam;
	}

	// Redis의 게임 데이터 정리
	private void cleanupGameRedisData(Long roomId, BattleType type) {
		// 먼저 각 방의 유저 목록을 가져옴
		String roomUsersKey = type == BattleType.S ?
			RedisKeys.SingleRoomUsers(roomId) :
			RedisKeys.TeamRoomUsers(roomId);

		List<String> userIds = redisTemplate.opsForList().range(roomUsersKey, 0, -1);

		// 모든 삭제할 키를 리스트에 추가
		// 기존 키들 추가
		List<String> keysToDelete;
		if (type == BattleType.S) {
			keysToDelete = new ArrayList<>(Arrays.asList(
				roomUsersKey,
				RedisKeys.SingleRoomStatus(roomId)
			));
			if (userIds != null) {
				userIds.forEach(userId -> keysToDelete.add(RedisKeys.userSingleRoom(Long.parseLong(userId))));
			}
		} else {
			keysToDelete = new ArrayList<>(Arrays.asList(
				roomUsersKey,
				RedisKeys.TeamRoomStatus(roomId),
				RedisKeys.TeamMatchId(roomId),
				RedisKeys.getRoomDbId(roomId),
				RedisKeys.getRoomRedisId(roomId)
			));

			if (userIds != null) {
				userIds.forEach(userId -> keysToDelete.add(RedisKeys.userTeamRoom(Long.parseLong(userId))));
			}
		}
		redisTemplate.delete(keysToDelete);
	}

	private void removeUserRedisData(User user, String roomUsersKey, BattleType type) {

		// Redis에서 유저 정보 삭제
		redisTemplate.execute(new SessionCallback<List<Object>>() {
			public List<Object> execute(RedisOperations operations) {
				operations.multi();

				// Redis 작업들
				operations.opsForList().remove(roomUsersKey, 0, user.getUserId().toString());
				operations.delete(type == BattleType.T ?
					RedisKeys.userTeamRoom(user.getUserId()) :
					RedisKeys.userSingleRoom(user.getUserId()));
				return operations.exec();
			}
		});
	}

	private void handleTeamGameLeave(Game game, User user, Long roomId, String eventType, boolean isInProgress) {
		String redisRoomId = redisTemplate.opsForValue().get(RedisKeys.userTeamRoom(user.getUserId()));
		String roomUsersKey = RedisKeys.TeamRoomUsers(Long.parseLong(Objects.requireNonNull(redisRoomId)));

		removeUserRedisData(user, roomUsersKey, BattleType.T);

		// 남은 유저 정보 조회 및 팀별 그룹화
		Map<Long, List<UserInfoResponseDto>> remainingUsersByTeam = getRemainingTeamUsers(redisRoomId,
			user.getUserId());

		String matchId = redisTemplate.opsForValue().get(RedisKeys.TeamMatchId(Long.parseLong(redisRoomId)));
		sendTeamLeaveEvent(game, roomId, user, remainingUsersByTeam, matchId, eventType);

		// 팀 전체가 나간 경우 처리
		if (!remainingUsersByTeam.containsKey(roomId)) {
			handleTeamAllLeft(game, roomId, redisRoomId, matchId, remainingUsersByTeam, isInProgress);
		}

	}

	private void sendTeamLeaveEvent(Game game, Long roomId, User user,
		Map<Long, List<UserInfoResponseDto>> remainingUsersByTeam, String matchId, String eventType) {
		TeamGameLeaveResponseDto responseDto = TeamGameLeaveResponseDto.of(
			game.getId(),
			roomId,
			UserInfoResponseDto.fromEntity(user),
			remainingUsersByTeam
		);

		roomWebSocketService.sendWebSocketMessage(
			matchId,
			eventType,
			responseDto,
			BattleType.T
		);
	}

	private void handleTeamAllLeft(Game game, Long roomId, String redisRoomId, String matchId,
		Map<Long, List<UserInfoResponseDto>> remainingUsersByTeam, boolean isInProgress) {

		String opposingRoomId = matchId.replace(redisRoomId, "").replace("-", "");
		String opposingStatus = redisTemplate.opsForValue()
			.get(RedisKeys.TeamRoomStatus(Long.parseLong(opposingRoomId)));

		// 상태 변경
		redisTemplate.opsForValue().set(RedisKeys.TeamRoomStatus(Long.parseLong(redisRoomId)), TEAM_LEFT_STATUS);

		// 상대팀이 이미 전부 나간 상태인 경우
		if (TEAM_LEFT_STATUS.equals(opposingStatus)) {
			// Redis 데이터 정리
			cleanupGameRedisData(Long.parseLong(redisRoomId), BattleType.T);
			cleanupGameRedisData(Long.parseLong(opposingRoomId), BattleType.T);
		} else if (isInProgress) {
			// 게임 진행중인 경우에만 수행하는 로직
			handleInProgressTeamAllLeft(game, roomId, matchId, remainingUsersByTeam);
		}

	}

	private void handleInProgressTeamAllLeft(Game game, Long roomId, String matchId,
		Map<Long, List<UserInfoResponseDto>> remainingUsersByTeam) {

		game.cancelGame();

		// 승/패 처리
		Long winningTeamId = remainingUsersByTeam.keySet().iterator().next();
		TeamRoom winningTeam = teamRoomRepository.findById(winningTeamId)
			.orElseThrow(RoomNotFoundException::new);
		winningTeam.updateStatusByGameWinWithOutSolve(BattleResult.FIRST);

		TeamRoom losingTeam = teamRoomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);
		losingTeam.updateStatusByGameLose(BattleResult.SECOND);

		teamRoomRepository.saveAll(Arrays.asList(winningTeam, losingTeam));

		// 게임 종료 이벤트 전송
		Map<String, Object> gameEndPayload = Map.of(
			"gameId", game.getId(),
			"reason", "ENEMY_ALL_LEFT",
			"winningTeamId", winningTeamId
		);

		roomWebSocketService.sendWebSocketMessage(
			matchId,
			"GAME_END",
			gameEndPayload,
			BattleType.T
		);
	}

	private void handleSingleGameLeave(Game game, User user, Long roomId, String eventType, boolean isInProgress) {
		String redisRoomId = redisTemplate.opsForValue().get(RedisKeys.userSingleRoom(user.getUserId()));
		String roomUsersKey = RedisKeys.SingleRoomUsers(Long.parseLong(Objects.requireNonNull(redisRoomId)));

		removeUserRedisData(user, roomUsersKey, BattleType.S);

		List<String> remainingUserIds = redisTemplate.opsForList().range(roomUsersKey, 0, -1);
		List<UserInfoResponseDto> remainingUsers = getRemainingUsers(remainingUserIds);

		SingleRoom singleRoom = singleRoomRepository.findById(roomId)
			.orElseThrow(RoomNotFoundException::new);

		// finishTime이 null이고 battleResult도 null이면 아직 문제를 못 푼 상태
		if (isInProgress && singleRoom.getFinishTime() == null && singleRoom.getBattleResult() == null) {
			singleRoom.updateStatusByGameLose(BattleResult.FAIL);
		}

		// 공통 3: 이벤트 전송
		sendSingleLeaveEvent(game, roomId, user, remainingUsers, eventType);

		// 모든 유저가 퇴장한 경우
		if (remainingUsers.isEmpty()) {
			handleSingleAllLeft(game, redisRoomId, isInProgress);
		}
	}

	private void sendSingleLeaveEvent(Game game, Long roomId, User user,
		List<UserInfoResponseDto> remainingUsers, String eventType) {
		SingleGameLeaveResponseDto responseDto = SingleGameLeaveResponseDto.of(
			game.getId(),
			roomId,
			UserInfoResponseDto.fromEntity(user),
			remainingUsers
		);

		roomWebSocketService.sendWebSocketMessage(
			game.getId().toString(),
			eventType,
			responseDto,
			BattleType.S
		);
	}

	private void handleSingleAllLeft(Game game, String redisRoomId, boolean isInProgress) {
		if (isInProgress) {
			game.cancelGame();

			Map<String, Object> gameEndPayload = Map.of(
				"gameId", game.getId(),
				"reason", "ALL_PLAYERS_LEFT"
			);

			roomWebSocketService.sendWebSocketMessage(
				game.getId().toString(),
				"GAME_END",
				gameEndPayload,
				BattleType.S
			);
		}

		cleanupGameRedisData(Long.parseLong(redisRoomId), BattleType.S);
	}
}
