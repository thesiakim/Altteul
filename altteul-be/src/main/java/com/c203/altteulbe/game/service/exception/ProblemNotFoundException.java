package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class ProblemNotFoundException extends BusinessException {
	public ProblemNotFoundException() {
		super("존재하지 않는 문제입니다.", HttpStatus.NOT_FOUND);
	}
}
