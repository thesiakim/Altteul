package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class RoomNotInGamingStateException extends BusinessException {
	public RoomNotInGamingStateException() {
		super("게임중이 아닙니다.", HttpStatus.BAD_REQUEST, 465);
	}
}
