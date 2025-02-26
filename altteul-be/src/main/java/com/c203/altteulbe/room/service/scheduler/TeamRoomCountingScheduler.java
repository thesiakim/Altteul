package com.c203.altteulbe.room.service.scheduler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.common.annotation.DistributedLock;
import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.utils.RedisKeys;
import com.c203.altteulbe.room.persistent.repository.team.TeamRoomRedisRepository;
import com.c203.altteulbe.room.service.RoomValidator;
import com.c203.altteulbe.room.service.RoomWebSocketService;
import com.c203.altteulbe.room.service.TeamRoomService;
import com.c203.altteulbe.room.util.MatchKeyParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamRoomCountingScheduler {
	private final RedisTemplate<String, String> redisTemplate;
	private final TeamRoomService teamRoomService;
	private final TeamRoomRedisRepository teamRoomRedisRepository;
	private final RoomValidator teamRoomValidator;
	private final RoomWebSocketService roomWebSocketService;

	// 1초마다 실행
	@Scheduled(fixedRate = 1000)
	public void counting() {
		processCounting();  // AOP 적용을 위해 별도 메서드로 호출
	}

	@DistributedLock(key = "team_room_counting_lock")
	public void processCounting() {

		// Redis에서 팀전 방의 카운팅 키 조회
		Set<String> activeRooms = redisTemplate.keys("room:team:*:countdown");
		if (activeRooms == null || activeRooms.isEmpty()) {
			return;
		}

		// 카운팅해야 하는 팀전 방 탐색
		for (String roomKey : activeRooms) {
			String matchId = roomKey.split(":")[2];
			String[] roomIds = MatchKeyParser.getRoomIdPairs(matchId);
			Long roomId1 = Long.parseLong(roomIds[0]);
			Long roomId2 = Long.parseLong(roomIds[1]);
			Integer remainingTime = Integer.parseInt(redisTemplate.opsForValue().get(roomKey));

			// Redis에서 현재 방에 남아있는 유저 조회
			String roomUsersKey1 = RedisKeys.TeamRoomUsers(roomId1);
			String roomUsersKey2 = RedisKeys.TeamRoomUsers(roomId2);
			List<String> userIds1 = redisTemplate.opsForList().range(roomUsersKey1, 0, -1);
			List<String> userIds2 = redisTemplate.opsForList().range(roomUsersKey2, 0, -1);

			// 인원 검증 : 방을 이전 상태로 되돌릴 것이라고 가정하고 구현했기 때문에 관련 redis key는 카운팅만 제거
			if (!teamRoomValidator.isEnoughUsers(roomId1, BattleType.T)) {
				log.info("[TeamScheduler] 카운팅 중 최소 인원 미달 : roomId1 : {}", roomId1);
				roomWebSocketService.sendWebSocketMessageWithNote(String.valueOf(matchId),"COUNTING_CANCEL", "인원 수가 부족합니다.", BattleType.T);
				redisTemplate.delete(roomKey);
				continue;
			}
			if (!teamRoomValidator.isEnoughUsers(roomId2, BattleType.T)) {
				log.info("[TeamScheduler] 카운팅 중 최소 인원 미달 : roomId2 : {}", roomId2);
				roomWebSocketService.sendWebSocketMessageWithNote(String.valueOf(matchId),"COUNTING_CANCEL", "인원 수가 부족합니다.", BattleType.T);
				redisTemplate.delete(roomKey);
				continue;
			}

			// 카운팅 중 팀의 모든 유저가 퇴장한 경우 해당 방과 관련된 redis 데이터 삭제
			if (userIds1 == null || userIds1.isEmpty()) {
				log.info("[TeamScheduler] 카운팅 중 팀의 유저들 모두 퇴장 : roomId1 : {}", roomId1);
				redisTemplate.delete(roomKey);
				teamRoomRedisRepository.deleteRedisTeamRoom(roomId1);
				continue;
			}
			if (userIds2 == null || userIds2.isEmpty()) {
				log.info("[TeamScheduler] 카운팅 중 각 팀의 유저들 모두 퇴장 : roomId2 : {}", roomId2);
				redisTemplate.delete(roomKey);
				teamRoomRedisRepository.deleteRedisTeamRoom(roomId2);
				continue;
			}

			// 카운팅이 마이너스가 되는 순간 카운팅 키 삭제 후 게임 시작 처리
			if (remainingTime < 0) {
				log.info("[TeamScheduler] 카운팅 정상 완료 : roomId1 = {}, roomId2 = {}", roomId1, roomId2);
				redisTemplate.delete(roomKey);
				teamRoomService.startGameAfterCountDown(matchId, roomId1, roomId2);
				continue;
			}

			Map<String, String> timePayload = Map.of("time", String.valueOf(remainingTime));
			roomWebSocketService.sendWebSocketMessage(String.valueOf(matchId), "COUNTING", timePayload, BattleType.T);

			log.info("[TeamScheduler] 카운팅 진행 중 : {}초", remainingTime);
			redisTemplate.opsForValue().set(roomKey, String.valueOf(remainingTime-1));
		}
	}
}
