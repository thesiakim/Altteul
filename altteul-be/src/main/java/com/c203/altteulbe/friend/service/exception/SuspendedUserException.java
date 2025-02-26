package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class SuspendedUserException extends BusinessException {
	public SuspendedUserException() {
		super("정지된 사용자입니다.", HttpStatus.BAD_REQUEST);
	}
}
