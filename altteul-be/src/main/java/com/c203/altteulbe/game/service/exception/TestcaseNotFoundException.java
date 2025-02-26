package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class TestcaseNotFoundException extends BusinessException {
	public TestcaseNotFoundException() {
		super("존재하지 않는 테스트케이스입니다.", HttpStatus.NOT_FOUND);
	}
}
