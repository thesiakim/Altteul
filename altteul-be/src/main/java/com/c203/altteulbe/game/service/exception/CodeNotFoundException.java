package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class CodeNotFoundException extends BusinessException {
	public CodeNotFoundException() {
		super("코드를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
