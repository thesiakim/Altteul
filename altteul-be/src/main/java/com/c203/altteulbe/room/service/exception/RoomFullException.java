package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class RoomFullException extends BusinessException {
	public RoomFullException() {
		super("최대 4명까지 입장 가능합니다.", HttpStatus.BAD_REQUEST, 463);
	}
}
