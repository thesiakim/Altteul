package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class RoomNotFoundException extends BusinessException {
	public RoomNotFoundException() {
		super("존재하지 않는 방입니다.", HttpStatus.NOT_FOUND, 464);
	}
}
