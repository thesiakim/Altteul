package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class CannotLeaveRoomException extends BusinessException {
	public CannotLeaveRoomException() {
		super("대기 중인 방에서만 퇴장할 수 있습니다.", HttpStatus.BAD_REQUEST, 454);
	}
}
