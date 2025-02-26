package com.c203.altteulbe.chat.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class MessageContentRequiredException extends BusinessException {
	public MessageContentRequiredException() {
		super("메시지 내용은 필수입니다.", HttpStatus.BAD_REQUEST);
	}
}
