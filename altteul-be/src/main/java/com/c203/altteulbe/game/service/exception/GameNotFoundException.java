package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class GameNotFoundException extends BusinessException {
	public GameNotFoundException() {
		super("게임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	}
}
