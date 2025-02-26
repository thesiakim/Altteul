package com.c203.altteulbe.chat.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class SelfChatroomCreateException extends BusinessException {
	public SelfChatroomCreateException() {
		super("자기 자신과는 방을 만들 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
