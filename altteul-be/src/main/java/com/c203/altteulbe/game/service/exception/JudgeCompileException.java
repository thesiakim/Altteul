package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class JudgeCompileException extends BusinessException {
	public JudgeCompileException(String message) { super(message, HttpStatus.BAD_REQUEST); }
}