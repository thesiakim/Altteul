package com.c203.altteulbe.room.service;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.websocket.dto.response.WebSocketResponse;
import com.c203.altteulbe.websocket.exception.WebSocketMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomWebSocketService {
	private final SimpMessagingTemplate messagingTemplate;
	private final ObjectMapper objectMapper;

	public <T> void sendWebSocketMessage(String roomId, String eventType, T responseDto, BattleType type) {
		String destination = getWebSocketDestination(roomId, type);
		sendMessage(destination, eventType, responseDto);
	}

	public void sendWebSocketMessage(String roomId, String eventType, String message, BattleType type) {
		String destination = getWebSocketDestination(roomId, type);
		sendMessage(destination, eventType, message);
	}

	public void sendWebSocketMessage(String destination, String eventType, String message) {
		sendMessage(destination, eventType, message);
	}

	public <T> void sendWebSocketMessage(String destination, String eventType, T payload) {
		try {
			sendMessage(destination, eventType, payload);
		} catch (Exception e) {
			log.error("WebSocket 메시지 변환 실패 (eventType: {}, destination: {}): {}",
				eventType, destination, e.getMessage());
			throw new WebSocketMessageException();
		}
	}

	// 문자열을 받아서 { note : ".." } 으로 변환
	public void sendWebSocketMessageWithNote(String roomId, String eventType, String message, BattleType type) {
		Map<String, String> payload = createNotePayload(message);
		sendWebSocketMessage(roomId, eventType, payload, type);
	}

	public void sendWebSocketMessageWithNote(String destination, String eventType, String message) {
		Map<String, String> payload = createNotePayload(message);
		sendWebSocketMessage(destination, eventType, payload);
	}

	public Map<String, String> createNotePayload(String message) {
		return Map.of("note", message);
	}

	private <T> void sendMessage(String destination, String eventType, T payload) {
		try {
			messagingTemplate.convertAndSend(destination, WebSocketResponse.withData(eventType, payload));
		} catch (Exception e) {
			log.error("WebSocket 메시지 전송 실패 (eventType: {}, destination: {}): {}", eventType, destination, e.getMessage());
			throw new WebSocketMessageException();
		}
	}


	private String getWebSocketDestination(String roomId, BattleType type) {
		return (type == BattleType.S) ? "/sub/single/room/" + roomId : "/sub/team/room/" + roomId;
	}
}


