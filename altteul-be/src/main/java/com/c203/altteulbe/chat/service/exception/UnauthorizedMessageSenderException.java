package com.c203.altteulbe.chat.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class UnauthorizedMessageSenderException extends BusinessException {
	public UnauthorizedMessageSenderException() {
		super("메시지 발신자가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
	}
}
