package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class UserNotInRoomException extends BusinessException {
	public UserNotInRoomException() {
		super("방에 존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST, 467);
	}
}
