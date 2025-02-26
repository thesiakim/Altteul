package com.c203.altteulbe.room.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.utils.RedisKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomValidator {
	private final RedisTemplate<String, String> redisTemplate;

	// 유저가 방에 존재하는지 검증
	public boolean isUserInAnyRoom(Long userId, BattleType type) {
		String roomKey = switch (type) {
			case S -> RedisKeys.userSingleRoom(userId);
			case T -> RedisKeys.userTeamRoom(userId);
		};
		return Boolean.TRUE.equals(redisTemplate.hasKey(roomKey));
	}

	// 유저가 특정 방에 존재하는지 검증
	public boolean isUserInThisRoom(Long userId, Long roomId, BattleType type) {
		String roomKey = switch (type) {
			case S -> RedisKeys.SingleRoomUsers(roomId);
			case T -> RedisKeys.TeamRoomUsers(roomId);
		};
		// Redis에서 해당 방의 유저 목록 조회
		List<String> users = redisTemplate.opsForList().range(roomKey, 0, -1);
		return users != null && users.contains(String.valueOf(userId));
	}

	// 방 상태 검증 (대기 여부)
	public boolean isRoomWaiting(Long roomId, BattleType type) {
		String roomStatus = switch (type) {
			case S -> redisTemplate.opsForValue().get(RedisKeys.SingleRoomStatus(roomId));
			case T -> redisTemplate.opsForValue().get(RedisKeys.TeamRoomStatus(roomId));
		};
		return "waiting".equals(roomStatus);
	}

	// 방 상태 검증 (매칭 완료 여부)
	public boolean isRoomMatched(Long roomId) {
		String roomStatus = redisTemplate.opsForValue().get(RedisKeys.TeamRoomStatus(roomId));
		return "matched".equals(roomStatus);
	}

	// 팀이 게임 중인지 검증
	public boolean isRoomGaming(Long roomId) {
		String roomStatus = redisTemplate.opsForValue().get(RedisKeys.TeamRoomStatus(roomId));
		return "gaming".equals(roomStatus);
	}

	// 방장 검증
	public boolean isRoomLeader(Long roomId, Long leaderId, BattleType type) {
		String roomUsersKey = switch (type) {
			case S -> RedisKeys.SingleRoomUsers(roomId);
			case T -> RedisKeys.TeamRoomUsers(roomId);
		};

		String savedLeaderId = redisTemplate.opsForList().index(roomUsersKey, 0);
		return savedLeaderId != null && savedLeaderId.equals(leaderId.toString());
	}

	// 최소 인원 검증
	public boolean isEnoughUsers(Long roomId, BattleType type) {
		String roomUsersKey =
			(type == BattleType.S) ? RedisKeys.SingleRoomUsers(roomId) : RedisKeys.TeamRoomUsers(roomId);
		Long userCount = redisTemplate.opsForList().size(roomUsersKey);

		return userCount != null && userCount >= type.getMinUsers() && userCount <= type.getMaxUsers();
	}

	// 유저가 해당 방에 속해있는지 검증
	public boolean isUserInGamingRoom(Long roomId, Long userId) {
		// 유저가 팀 방에 존재하는지 확인
		if (!isUserInAnyRoom(userId, BattleType.T)) {
			return false;
		}

		// 해당 방에 속한 유저인지 확인
		String userRoomKey = RedisKeys.userTeamRoom(userId);
		String currentRoomId = redisTemplate.opsForValue().get(userRoomKey);

		return roomId.toString().equals(currentRoomId);
	}
}
