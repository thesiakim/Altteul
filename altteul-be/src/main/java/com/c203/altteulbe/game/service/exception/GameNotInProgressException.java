package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class GameNotInProgressException extends BusinessException {
	public GameNotInProgressException() {
		super("게임이 진행중이 아닙니다.", HttpStatus.BAD_REQUEST);
	}
}
