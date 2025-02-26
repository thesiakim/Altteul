package com.c203.altteulbe.game.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.c203.altteulbe.game.service.judge.JudgeService;
import com.c203.altteulbe.game.web.dto.judge.request.SubmitCodeRequestDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class JudgeWebSocketController {

	private final JudgeService judgeService;

	@MessageMapping("/judge/submition")
	public void handleSubmission(@Payload SubmitCodeRequestDto message, SimpMessageHeaderAccessor headerAccessor) {
		Long id = getUserIdFromHeaderAccessor(headerAccessor);
		judgeService.submitCode(message, id);
	}

	private static long getUserIdFromHeaderAccessor(SimpMessageHeaderAccessor headerAccessor) {
		return Long.parseLong(headerAccessor.getSessionAttributes().get("userId").toString());
	}

}
