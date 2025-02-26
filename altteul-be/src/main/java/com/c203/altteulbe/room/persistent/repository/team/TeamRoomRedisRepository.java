package com.c203.altteulbe.room.persistent.repository.team;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.common.annotation.DistributedLock;
import com.c203.altteulbe.common.utils.RedisKeys;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRedisRepository;
import com.c203.altteulbe.room.web.dto.response.RoomEnterResponseDto;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.web.dto.response.UserInfoResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TeamRoomRedisRepository {
	private final RedisTemplate<String, String> redisTemplate;
	private final UserRepository userRepository;
	private final SingleRoomRedisRepository singleRoomRedisRepository;

	// 입장 가능한 대기방 조회
	public Long getAvailableRoom() {
		Set<String> roomIds = redisTemplate.opsForZSet().reverseRange(RedisKeys.TEAM_WAITING_ROOMS, 0, -1);
		if (roomIds == null || roomIds.isEmpty()) {
			return null;
		}
		for (String roomId : roomIds) {
			String roomStatusKey = RedisKeys.TeamRoomStatus(Long.parseLong(roomId));  // 방 상태
			String roomUserKey = RedisKeys.TeamRoomUsers(Long.parseLong(roomId));

			String status = redisTemplate.opsForValue().get(roomStatusKey);
			Long userCount = redisTemplate.opsForList().size(roomUserKey);

			if ("waiting".equals(status) && userCount != null && userCount<4) {
				return Long.parseLong(roomId);
			}
		}
		return null;
	}

	// 유저가 속한 방 조회
	public Long getRoomIdByUser(Long userId) {
		String roomIdStr = redisTemplate.opsForValue().get(RedisKeys.userTeamRoom(userId));
		return (roomIdStr != null) ? Long.parseLong(roomIdStr) : null;
	}

	// 방의 상태 조회
	public String getRoomStatus(Long roomId) {
		String roomStatusKey = RedisKeys.TeamRoomStatus(roomId);
		return redisTemplate.opsForValue().get(roomStatusKey);
	}

	// 팀전 대기방 생성
	public RoomEnterResponseDto createRedisTeamRoom(User user) {
		Long roomId = singleRoomRedisRepository.generateUniqueRoomId();
		log.info("팀전 대기방 생성 : roomId = {}", roomId);

		String roomStatusKey = RedisKeys.TeamRoomStatus(roomId);
		String roomUsersKey = RedisKeys.TeamRoomUsers(roomId);

		redisTemplate.opsForValue().set(roomStatusKey, "waiting");     // 대기 중 상태로 방 저장
		redisTemplate.opsForList().rightPush(roomUsersKey, user.getUserId().toString());
		redisTemplate.opsForZSet().add(RedisKeys.TEAM_WAITING_ROOMS, roomId.toString(), System.currentTimeMillis());  // 생성된 순서로 대기방 저장
		redisTemplate.opsForValue().set(RedisKeys.userTeamRoom(user.getUserId()), roomId.toString());

		List<UserInfoResponseDto> users = List.of(UserInfoResponseDto.fromEntity(user));   // List로 변환
		return RoomEnterResponseDto.from(roomId, user.getUserId(), users);
	}

	// 기존 대기방에 유저 추가
	@DistributedLock(key = "#roomId")
	public RoomEnterResponseDto insertUserToExistingRoom(Long roomId, User user) {
		String roomUsersKey = RedisKeys.TeamRoomUsers(roomId);

		// Redis에 유저 추가
		redisTemplate.opsForList().rightPush(roomUsersKey, user.getUserId().toString());              // 유저 추가 (방장 위임을 위해 순서 유지)
		redisTemplate.opsForValue().set(RedisKeys.userTeamRoom(user.getUserId()), roomId.toString()); // 유저가 속한 방 저장

		String leaderId = redisTemplate.opsForList().index(roomUsersKey, 0);             // 방장 검색
		List<String> userIds = redisTemplate.opsForList().range(roomUsersKey, 0, -1); // 현재 방의 모든 유저 조회

		return convertToRoomEnterResponseDto(roomId, leaderId, userIds);
	}

	// User를 RoomEnterResponseDto로 변환
	public RoomEnterResponseDto convertToRoomEnterResponseDto(Long roomId, String leaderId, List<String> userIds) {
		List<User> users = userRepository.findByUserIdIn(
			userIds.stream().map(Long::parseLong).collect(Collectors.toList())
		);

		// 조회된 users를 userId 기준으로 Map으로 변환
		Map<Long, User> userMap = users.stream()
			.collect(Collectors.toMap(User::getUserId, Function.identity()));

		// Redis 순서대로 정렬
		List<User> sortedUsers = userIds.stream()
									.map(id -> userMap.get(Long.parseLong(id)))  // Redis 순서 유지
									.collect(Collectors.toList());

		List<UserInfoResponseDto> userDtos = UserInfoResponseDto.fromEntities(sortedUsers);
		return RoomEnterResponseDto.from(roomId, Long.parseLong(leaderId), userDtos);
	}


	// 카운팅 중 유저가 모두 퇴장한 경우 → 팀전 방 데이터 삭제
	public void deleteRedisTeamRoom(Long roomId) {
		String roomUsersKey = RedisKeys.TeamRoomUsers(roomId);
		String roomStatusKey = RedisKeys.TeamRoomStatus(roomId);

		redisTemplate.delete(roomUsersKey);  // 방에 속한 유저 삭제
		redisTemplate.delete(roomStatusKey); // 방 상태 삭제
		redisTemplate.opsForZSet().remove(RedisKeys.TEAM_WAITING_ROOMS, roomId.toString()); // 대기방 목록에서 제거
		log.info("모든 유저들이 퇴장한 팀전 방의 데이터 삭제 : roomId = {}", roomId);
	}
}
