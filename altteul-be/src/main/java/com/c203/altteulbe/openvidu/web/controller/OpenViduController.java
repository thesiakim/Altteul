package com.c203.altteulbe.openvidu.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.ResponseBody;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook;

@RestController
@RequestMapping("/api")
public class OpenViduController {

	@Value("${livekit.api.key}")
	private String LIVEKIT_API_KEY;

	@Value("${livekit.api.secret}")
	private String LIVEKIT_API_SECRET;

	@PostMapping(value = "/openvidu/token")
	public ApiResponseEntity<?> createToken(@RequestBody Map<String, String> params) {
		String roomName = params.get("roomName");
		String participantName = params.get("participantName");

		if (roomName == null || participantName == null) {
			return ApiResponse.error(
				(ResponseBody.Failure)Map.of("errorMessage", "roomName and participantName are required"),
				HttpStatus.BAD_REQUEST);
		}

		AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
		token.setName(participantName);
		token.setIdentity(participantName);
		token.addGrants(new RoomJoin(true), new RoomName(roomName));

		return ApiResponse.success(Map.of("token", token.toJwt()));
	}

	@PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
	public ResponseEntity<String> receiveWebhook(@RequestHeader("Authorization") String authHeader,
		@RequestBody String body) {
		WebhookReceiver webhookReceiver = new WebhookReceiver(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
		try {
			LivekitWebhook.WebhookEvent event = webhookReceiver.receive(body, authHeader);
			System.out.println("LiveKit Webhook: " + event.toString());
		} catch (Exception e) {
			System.err.println("Error validating webhook event: " + e.getMessage());
		}
		return ResponseEntity.ok("ok");
	}
}
