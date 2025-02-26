package com.c203.altteulbe.room.service;

import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRedisRepository;
import com.c203.altteulbe.room.service.scheduler.SingleRoomCountingScheduler;

@ExtendWith(MockitoExtension.class)
class SingleRoomCountingSchedulerTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private SingleRoomService singleRoomService;

	@Mock
	private RoomValidator singleRoomValidator;

	@Mock
	private RoomWebSocketService roomWebSocketService;

	@Mock
	private SingleRoomRedisRepository singleRoomRedisRepository;

	@InjectMocks
	private SingleRoomCountingScheduler scheduler;

	@Test
	void 웹소켓으로_카운트다운_숫자_전송_테스트() {
		// Given
		Set<String> activeRooms = Set.of("room:single:1:countdown");

		// Redis mock 설정
		when(redisTemplate.keys("room:single:*:countdown")).thenReturn(activeRooms);

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("room:single:1:countdown")).thenReturn("5");

		ListOperations<String, String> listOperations = mock(ListOperations.class);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.range("room:single:1:users", 0, -1)).thenReturn(List.of("1", "2")); // 유저 ID 목록

		when(singleRoomValidator.isEnoughUsers(anyLong(), eq(BattleType.S))).thenReturn(true);

		// When
		scheduler.counting();

		// Then
		verify(roomWebSocketService).sendWebSocketMessage(
			eq("1"),
			eq("COUNTING"),
			eq(5),
			eq(BattleType.S)
		);
	}

	@Test
	void 인원_부족_시_COUNTING_CANCEL_이벤트_전송_테스트() {
		// Given
		Set<String> activeRooms = Set.of("room:single:1:countdown");

		when(redisTemplate.keys("room:single:*:countdown")).thenReturn(activeRooms);

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("room:single:1:countdown")).thenReturn("5");

		ListOperations<String, String> listOperations = mock(ListOperations.class);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.range("room:single:1:users", 0, -1)).thenReturn(List.of("1", "2"));

		when(singleRoomValidator.isEnoughUsers(anyLong(), eq(BattleType.S))).thenReturn(false);

		// When
		scheduler.counting();

		// Then
		verify(roomWebSocketService).sendWebSocketMessage(eq("1"), eq("COUNTING_CANCEL"), eq("인원 수가 부족합니다."), eq(BattleType.S));
		verify(redisTemplate).delete("room:single:1:countdown"); // 카운트다운 삭제 검증
	}

	@Test
	void 카운팅_중_모든_유저_퇴장_시_방_관련_데이터_삭제_테스트() {
		// Given
		Set<String> activeRooms = Set.of("room:single:1:countdown");

		when(redisTemplate.keys("room:single:*:countdown")).thenReturn(activeRooms);

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("room:single:1:countdown")).thenReturn("5");

		ListOperations<String, String> listOperations = mock(ListOperations.class);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.range("room:single:1:users", 0, -1)).thenReturn(List.of()); // 유저 0명

		// When
		scheduler.counting();

		// Then
		verify(redisTemplate).delete("room:single:1:countdown");         // 카운트다운 삭제
		verify(singleRoomRedisRepository).deleteRedisSingleRoom(1L);  // Redis 방 삭제 검증
	}

	@Test
	void 카운트_다운_완료_시_게임_시작_정보_전송_테스트() {
		// Given
		Set<String> activeRooms = Set.of("room:single:1:countdown");

		when(redisTemplate.keys("room:single:*:countdown")).thenReturn(activeRooms);

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get("room:single:1:countdown")).thenReturn("-1");

		ListOperations<String, String> listOperations = mock(ListOperations.class);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.range("room:single:1:users", 0, -1)).thenReturn(List.of("1", "2"));

		when(singleRoomValidator.isEnoughUsers(anyLong(), eq(BattleType.S))).thenReturn(true);

		// When
		scheduler.counting();

		// Then
		verify(redisTemplate).delete("room:single:1:countdown"); // 카운트다운 키 삭제
		verify(singleRoomService).startGameAfterCountDown(1L); // 게임 시작 호출 확인
	}

}