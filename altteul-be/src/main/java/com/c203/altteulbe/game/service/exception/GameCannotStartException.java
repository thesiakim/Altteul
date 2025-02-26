package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class GameCannotStartException extends BusinessException {
	public GameCannotStartException() {
		super("게임을 시작할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST, 468);
	}
}
