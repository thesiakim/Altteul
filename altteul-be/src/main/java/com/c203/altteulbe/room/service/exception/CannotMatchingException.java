package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class CannotMatchingException extends BusinessException {
	public CannotMatchingException() {
		super("대기 중인 방만 매칭을 시작할 수 있습니다.", HttpStatus.BAD_REQUEST, 456);
	}
}

