package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class WithdrawUserException extends BusinessException {
	public WithdrawUserException() {
		super("탈퇴한 사용자입니다.", HttpStatus.BAD_REQUEST);
	}
}
