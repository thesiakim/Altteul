package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotRoomLeaderException extends BusinessException {
	public NotRoomLeaderException() {
		super("방장이 아닙니다.", HttpStatus.BAD_REQUEST, 459);
	}
}
