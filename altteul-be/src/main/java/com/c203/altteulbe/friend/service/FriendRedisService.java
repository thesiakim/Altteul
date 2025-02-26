package com.c203.altteulbe.friend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.common.utils.RedisKeys;
import com.c203.altteulbe.friend.web.dto.response.FriendRequestResponseDto;
import com.c203.altteulbe.friend.web.dto.response.FriendResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRedisService {

	private final RedisTemplate<String, String> redisTemplate;

	// 친구 리스트 조회
	public List<FriendResponseDto> getCachedFriendList(Long userId) throws JsonProcessingException {
		String key = RedisKeys.getFriendListKey(userId);
		String jsonFriendList = redisTemplate.opsForValue().get(key);
		if (jsonFriendList == null) {
			return new ArrayList<>(); // 캐시에 데이터가 없으면 빈 리스트 반환
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonFriendList, new TypeReference<>() {
		});
	}

	// 친구 리스트 저장
	public void setFriendList(Long userId, List<FriendResponseDto> friendList) throws JsonProcessingException {
		String key = RedisKeys.getFriendListKey(userId);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonFriendList = objectMapper.writeValueAsString(friendList); // json 형식으로 저장
		redisTemplate.opsForValue().set(key, jsonFriendList, 30, TimeUnit.MINUTES);

	}

	// 친구 리스트 삭제
	public void invalidateFriendList(Long userId) {
		String key = RedisKeys.getFriendListKey(userId);
		redisTemplate.delete(key);
	}

	// 친구 관계 확인
	public Boolean checkFriendRelation(Long userId1, Long userId2) throws JsonProcessingException {
		String key = RedisKeys.getFriendRelationKey(userId1);
		String value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return false;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		List<Long> friendListIds = objectMapper.readValue(value, new TypeReference<>() {
		});
		return friendListIds.contains(userId2); // 캐시에 없으면 기본적으로 false 반환
	}

	// 친구 요청 목록 캐시 저장
	public void cacheFriendRequests(Long userId, List<FriendRequestResponseDto> requests) throws
		JsonProcessingException {
		String key = RedisKeys.geFriendRequestKey(userId);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonFriendRequestList = objectMapper.writeValueAsString(requests);
		redisTemplate.opsForValue().set(key, jsonFriendRequestList);
		redisTemplate.expire(key, 30, TimeUnit.MINUTES); // TTL 설정
	}

	// 친구 요청 목록 캐시 조회
	public List<FriendRequestResponseDto> getCachedFriendRequests(Long userId) throws JsonProcessingException {
		String key = RedisKeys.geFriendRequestKey(userId);
		String cachedData = redisTemplate.opsForValue().get(key);
		if (cachedData == null) {
			return new ArrayList<>();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(cachedData, new TypeReference<>() {
		});
	}

	// 친구 요청 캐시 무효화
	public void invalidateFriendRequests(Long userId) {
		String key = RedisKeys.geFriendRequestKey(userId);
		redisTemplate.delete(key);
	}

	// 캐시 무효화 처리
	public void invalidateCaches(Long userId, Long friendId) {
		// 친구 요청 목록 캐시 삭제
		invalidateFriendRequests(userId);

		// 친구 리스트 캐시 삭제
		invalidateFriendList(userId);
		invalidateFriendList(friendId);
	}
}
