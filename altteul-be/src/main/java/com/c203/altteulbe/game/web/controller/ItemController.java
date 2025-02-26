package com.c203.altteulbe.game.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.c203.altteulbe.game.service.ItemService;
import com.c203.altteulbe.game.web.dto.item.request.UseItemRequestDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@MessageMapping("/item/use")
	public void handle(@Payload UseItemRequestDto message, SimpMessageHeaderAccessor headerAccessor) {
		Long id = getUserIdFromHeaderAccessor(headerAccessor);
		itemService.useItem(message, id);
	}

	private static long getUserIdFromHeaderAccessor(SimpMessageHeaderAccessor headerAccessor) {
		return Long.parseLong(headerAccessor.getSessionAttributes().get("userId").toString());
	}

}
