package com.c203.altteulbe.chat.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotFoundChatroomException extends BusinessException {
	public NotFoundChatroomException() {
		super("채팅방이 없습니다.", HttpStatus.NOT_FOUND);
	}
}
