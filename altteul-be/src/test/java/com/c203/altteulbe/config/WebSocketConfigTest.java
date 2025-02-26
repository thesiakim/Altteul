package com.c203.altteulbe.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class WebSocketConfigTest {

	private WebSocketStompClient stompClient;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() {
		stompClient = new WebSocketStompClient(new StandardWebSocketClient());
	}

	// JWT 관련 부분 제외
	@Test
	void 웹소켓_연결이_정상적으로_이루어지는지_검증한다() throws Exception {
		String url = "ws://localhost:" + port + "/ws/websocket";
		StompSession session = stompClient.connect(url, new StompSessionHandlerAdapter() {})
			.get(1, TimeUnit.SECONDS);

		assertNotNull(session);
		assertTrue(session.isConnected());
	}

}