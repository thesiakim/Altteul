package com.c203.altteulbe.websocket;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.c203.altteulbe.friend.service.FriendRedisService;
import com.c203.altteulbe.friend.service.UserStatusService;
import com.c203.altteulbe.game.service.GameLeaveService;
import com.c203.altteulbe.game.web.dto.leave.request.GameLeaveRequestDto;
import com.c203.altteulbe.room.persistent.repository.single.SingleRoomRedisRepository;
import com.c203.altteulbe.room.persistent.repository.team.TeamRoomRedisRepository;
import com.c203.altteulbe.room.service.SingleRoomService;
import com.c203.altteulbe.room.service.TeamRoomService;
import com.c203.altteulbe.user.service.exception.NotFoundUserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
	private final UserStatusService userStatusService;
	private final SingleRoomRedisRepository singleRoomRedisRepository;
	private final TeamRoomRedisRepository teamRoomRedisRepository;
	private final SingleRoomService singleRoomService;
	private final TeamRoomService teamRoomService;
	private final GameLeaveService gameLeaveService;
	private final FriendRedisService friendRedisService;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if (sessionAttributes != null && sessionAttributes.containsKey("userId")) {
			try {
				Long userId = (Long)sessionAttributes.get("userId");
				userStatusService.setUserOnline(userId);
				log.info("유저가 연결 되었습니다 - userId: {}", userId);
			} catch (Exception e) {
				log.error("WebSocket 연결 처리 실패: {}", e.getMessage());
			}
		} else {
			log.info("WebSocket 연결 처리 실패: session attributes에서 userId를 찾을 수 없습니다.");
			throw new NotFoundUserException();
		}
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

		log.info(accessor.toString());

		if (sessionAttributes != null && sessionAttributes.containsKey("userId")) {
			try {
				Long userId = (Long)sessionAttributes.get("userId");

				Long singleRoomId = singleRoomRedisRepository.getRoomIdByUser(userId);
				Long teamRoomId = teamRoomRedisRepository.getRoomIdByUser(userId);

				if (userId != null && singleRoomId != null) {
					log.info("유저 {}가 개인전 대기방 {}에서 연결 해제 되었습니다.", userId, singleRoomId);
					singleRoomService.webSocketDisconnectLeave(singleRoomId, userId);
				}
				if (userId != null && teamRoomId != null) {
					log.info("유저 {}가 팀전 대기방 {}에서 연결 해제 되었습니다.", userId, teamRoomId);
					teamRoomService.webSocketDisconnectLeave(teamRoomId, userId);
				}
				if (userId != null && teamRoomId != null) {
					GameLeaveRequestDto dto = GameLeaveRequestDto.builder()
						.roomId(teamRoomId)
						.build();
					gameLeaveService.leaveGame(userId, dto);
					log.info("유저 {}가 팀전 게임방 {}에서 나갔습니다.", userId, teamRoomId);
				}
				if (userId != null && singleRoomId != null) {
					GameLeaveRequestDto dto = GameLeaveRequestDto.builder()
						.roomId(singleRoomId)
						.build();
					gameLeaveService.leaveGame(userId, dto);
					log.info("유저 {}가 개인전 게임방 {}에서 나갔습니다.", userId, teamRoomId);
				}
				userStatusService.setUserOffline(userId);
				friendRedisService.invalidateFriendList(userId);
				friendRedisService.invalidateFriendRequests(userId);
				log.info("유저가 연결 해제 되었습니다 - userId: {}", userId);
			} catch (Exception e) {
				log.error("WebSocket 연결 해제 처리 실패: {}", e.getMessage());
			}
		} else {
			log.info("WebSocket 연결 해제 처리 실패: session attributes에서 userId를 찾을 수 없습니다.");
			throw new NotFoundUserException();
		}
	}
}
