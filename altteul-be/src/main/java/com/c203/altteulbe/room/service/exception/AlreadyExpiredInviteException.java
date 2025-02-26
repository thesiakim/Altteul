package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AlreadyExpiredInviteException extends BusinessException {
	public AlreadyExpiredInviteException() {
		super("이미 만료된 초대입니다.", HttpStatus.BAD_REQUEST, 451);
	}
}
