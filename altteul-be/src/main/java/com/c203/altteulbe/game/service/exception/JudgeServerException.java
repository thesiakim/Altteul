package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class JudgeServerException extends BusinessException {
	public JudgeServerException() {
		super("채점 서버에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}