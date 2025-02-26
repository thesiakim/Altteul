package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AlreadyMatchingException extends BusinessException {
	public AlreadyMatchingException() {
		super("이미 매칭 중인 방입니다.", HttpStatus.BAD_REQUEST, 453);
	}
}
