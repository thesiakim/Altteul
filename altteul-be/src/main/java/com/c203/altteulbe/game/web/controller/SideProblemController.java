package com.c203.altteulbe.game.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.c203.altteulbe.game.service.side.SideProblemService;
import com.c203.altteulbe.game.web.dto.side.request.ReceiveSideProblemRequestDto;
import com.c203.altteulbe.game.web.dto.side.request.SubmitSideProblemRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SideProblemController {

	private final SideProblemService sideProblemService;

	@MessageMapping("/side/submit")
	public void handleSubmission(@Payload SubmitSideProblemRequestDto message, SimpMessageHeaderAccessor headerAccessor) {
		Long id = getUserIdFromHeaderAccessor(headerAccessor);
		sideProblemService.submit(message, id);
	}

	@MessageMapping("/side/receive")
	public void handle(@Payload ReceiveSideProblemRequestDto message, SimpMessageHeaderAccessor headerAccessor) {
		Long id = getUserIdFromHeaderAccessor(headerAccessor);
		sideProblemService.receive(message, id);
	}

	private static long getUserIdFromHeaderAccessor(SimpMessageHeaderAccessor headerAccessor) {
		return Long.parseLong(headerAccessor.getSessionAttributes().get("userId").toString());
	}
}