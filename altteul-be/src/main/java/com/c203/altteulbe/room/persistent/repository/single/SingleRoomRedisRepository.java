package com.c203.altteulbe.room.persistent.repository.single;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.common.annotation.DistributedLock;
import com.c203.altteulbe.common.utils.RedisKeys;
import com.c203.altteulbe.room.web.dto.response.RoomEnterResponseDto;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.web.dto.response.UserInfoResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SingleRoomRedisRepository {
	private final RedisTemplate<String, String> redisTemplate;
	private final UserRepository userRepository;

	// 입장 가능한 대기방 조회
	public Long getAvailableRoom() {
		Set<String> roomIds = redisTemplate.opsForZSet().reverseRange(RedisKeys.SINGLE_WAITING_ROOMS, 0, -1);

		if (roomIds == null || roomIds.isEmpty()) {
			return null;
		}
		log.info("입장 가능한 대기방 조회를 위한 waiting 상태인 개인전 방 roomIds = {}", roomIds);
		for (String roomId : roomIds) {
			log.info("입장 가능한 대기방 조회 : roomId = {}", roomId);
			String roomStatusKey = RedisKeys.SingleRoomStatus(Long.parseLong(roomId));  // 방 상태
			String roomUserKey = RedisKeys.SingleRoomUsers(Long.parseLong(roomId));     // 방에 존재하는 유저

			String status = redisTemplate.opsForValue().get(roomStatusKey);
			Long userCount = redisTemplate.opsForList().size(roomUserKey);
			log.info("{}번 방에 대한 대한 status = {}", roomId, status);
			log.info("{}번 방에 대한 대한 userCount = {}", roomId, userCount);

			if ("waiting".equals(status) && userCount != null && userCount<8) {
				log.info("{}번 방에 입장 가능", roomId);
				return Long.parseLong(roomId);
			}
		}
		return null;
	}

	// 유저가 속한 방 조회
	public Long getRoomIdByUser(Long userId) {
		String roomIdStr = redisTemplate.opsForValue().get(RedisKeys.userSingleRoom(userId));
		return (roomIdStr != null) ? Long.parseLong(roomIdStr) : null;
	}

	// 개인전 대기방 생성
	public RoomEnterResponseDto createRedisSingleRoom(User user) {
		Long roomId = generateUniqueRoomId();
		log.info("개인전 대기방 생성 : roomId = {}", roomId);

		String roomStatusKey = RedisKeys.SingleRoomStatus(roomId);
		String roomUsersKey = RedisKeys.SingleRoomUsers(roomId);

		redisTemplate.opsForValue().set(roomStatusKey, "waiting");     // 대기 중 상태로 방 저장
		redisTemplate.opsForList().rightPush(roomUsersKey, user.getUserId().toString());
		redisTemplate.opsForZSet().add(RedisKeys.SINGLE_WAITING_ROOMS, roomId.toString(), System.currentTimeMillis());  // 생성된 순서로 대기방 저장
		redisTemplate.opsForValue().set(RedisKeys.userSingleRoom(user.getUserId()), roomId.toString());

		List<UserInfoResponseDto> users = List.of(UserInfoResponseDto.fromEntity(user));   // List로 변환
		return RoomEnterResponseDto.from(roomId, user.getUserId(), users);
	}

	// 기존 대기방에 유저 추가
	@DistributedLock(key = "#roomId")
	public RoomEnterResponseDto insertUserToExistingRoom(Long roomId, User user) {
		String roomUsersKey = RedisKeys.SingleRoomUsers(roomId);

		// Redis에 유저 추가
		redisTemplate.opsForList().rightPush(roomUsersKey, user.getUserId().toString());                // 유저를 대기방에 추가 (방장 위임을 위해 순서 유지)
		redisTemplate.opsForValue().set(RedisKeys.userSingleRoom(user.getUserId()), roomId.toString()); // 유저가 속한 방 저장

		// 현재 방장 ID 가져오기
		String leaderId = redisTemplate.opsForList().index(roomUsersKey, 0);

		// 현재 방에 속한 모든 유저 ID 조회 (순서 보장)
		List<String> userIds = redisTemplate.opsForList().range(roomUsersKey, 0, -1);
		List<Long> userIdLongs = userIds.stream().map(Long::parseLong).collect(Collectors.toList());

		List<User> users = userRepository.findByUserIdIn(userIdLongs);

		// 조회된 users를 userIds 순서대로 정렬
		Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getUserId, Function.identity()));
		List<User> sortedUsers = userIdLongs.stream()
											.map(userMap::get)
											.collect(Collectors.toList());

		// DTO 변환
		List<UserInfoResponseDto> userDtos = UserInfoResponseDto.fromEntities(sortedUsers);
		return RoomEnterResponseDto.from(roomId, Long.parseLong(leaderId), userDtos);
	}


	// 카운팅 중 유저가 모두 퇴장한 경우 → 개인전 방 데이터 삭제
	public void deleteRedisSingleRoom(Long roomId) {
		String roomUsersKey = RedisKeys.SingleRoomUsers(roomId);
		String roomStatusKey = RedisKeys.SingleRoomStatus(roomId);

		redisTemplate.delete(roomUsersKey);  // 방에 속한 유저 삭제
		redisTemplate.delete(roomStatusKey); // 방 상태 삭제
		redisTemplate.opsForZSet().remove(RedisKeys.SINGLE_WAITING_ROOMS, roomId.toString()); // 대기방 목록에서 제거
		log.info("모든 유저들이 퇴장한 개인전 방의 데이터 삭제 : roomId = {}", roomId);
	}

	// roomId 생성 → DB 저장 시 game_id로 저장됨
	public Long generateUniqueRoomId() {
		long roomId = Math.abs(UUID.randomUUID().getMostSignificantBits()) % 1_000_000_000L; // 범위 제한
		log.info("generateUniqueRoomId 실행 : id = {}", roomId);
		return roomId;
	}
}
