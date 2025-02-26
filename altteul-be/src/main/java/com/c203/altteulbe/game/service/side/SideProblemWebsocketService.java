package com.c203.altteulbe.game.service.side;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.game.web.dto.side.response.ReceiveSideProblemResponseDto;
import com.c203.altteulbe.game.web.dto.side.response.SubmitSideProblemResponseDto;
import com.c203.altteulbe.websocket.dto.response.WebSocketResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SideProblemWebsocketService {
	private final SimpMessagingTemplate simpMessagingTemplate;

	public void sendSubmissionResult(SubmitSideProblemResponseDto responseDto,
		Long gameId, Long teamId) {
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/side-problem/result",
			WebSocketResponse.withData("사이드 문제 제출 결과", responseDto));
	}

	public void sendSideProblem(ReceiveSideProblemResponseDto responseDto,
		Long gameId, Long teamId) {
		simpMessagingTemplate.convertAndSend(
			"/sub/" + gameId + "/" + teamId + "/side-problem/receive",
			WebSocketResponse.withData("출제된 사이드 문제",responseDto));
	}
}
