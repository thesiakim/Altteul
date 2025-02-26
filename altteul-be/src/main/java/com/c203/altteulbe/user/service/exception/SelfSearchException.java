package com.c203.altteulbe.user.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class SelfSearchException extends BusinessException {
	public SelfSearchException() {
		super("자기 자신은 검색할 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
