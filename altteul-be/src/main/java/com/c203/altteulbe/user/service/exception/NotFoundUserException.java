package com.c203.altteulbe.user.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotFoundUserException extends BusinessException {
	public NotFoundUserException() {
		super("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	}
}
