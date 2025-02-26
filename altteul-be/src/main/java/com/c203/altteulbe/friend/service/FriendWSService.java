package com.c203.altteulbe.friend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.websocket.dto.response.WebSocketResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendWSService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	public <T> void sendRequestMessage(Long userId, T responseDto) {
		simpMessagingTemplate.convertAndSend("/sub/notification/" + userId,
			WebSocketResponse.withData("SEND_REQUEST", responseDto));
	}

	public void sendFriendListUpdateMessage(Long userId) {
		simpMessagingTemplate.convertAndSend(
			"/sub/friend/update/" + userId,
			WebSocketResponse.withData("FRIEND_RELATION_CHANGED", "FRIEND_LIST_UPDATE_REQUIRED")
		);
	}
}
