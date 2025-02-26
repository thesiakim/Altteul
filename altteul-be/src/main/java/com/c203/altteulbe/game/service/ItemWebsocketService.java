package com.c203.altteulbe.game.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.game.web.dto.item.response.UseItemResponseDto;
import com.c203.altteulbe.websocket.dto.response.WebSocketResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemWebsocketService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	public void hitItemByOther(UseItemResponseDto responseDto, Long gameId, Long teamId) {
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/item/hit",
			WebSocketResponse.withData("아이템 사용", responseDto));
	}
}
