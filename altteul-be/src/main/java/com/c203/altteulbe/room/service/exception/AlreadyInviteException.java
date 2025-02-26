package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AlreadyInviteException extends BusinessException {
	public AlreadyInviteException() {
		super("이미 초대한 친구입니다.", HttpStatus.BAD_REQUEST, 452);
	}
}
