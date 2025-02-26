package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AuthorizationException extends BusinessException {
	public AuthorizationException() {
		super("권한이 없습니다.", HttpStatus.FORBIDDEN);
	}
}
