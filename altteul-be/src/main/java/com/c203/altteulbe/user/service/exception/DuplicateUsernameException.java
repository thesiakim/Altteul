package com.c203.altteulbe.user.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class DuplicateUsernameException extends BusinessException {
	public DuplicateUsernameException() {
		super("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST);
	}
}
