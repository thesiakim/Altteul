package com.c203.altteulbe.websocket.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class WebSocketMessageException extends BusinessException {
	public WebSocketMessageException() {
		super("WebSocket 메시지 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
