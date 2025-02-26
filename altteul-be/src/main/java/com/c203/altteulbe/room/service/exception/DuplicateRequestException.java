package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class DuplicateRequestException extends BusinessException {
	public DuplicateRequestException() {
		super("이미 처리된 요청입니다.", HttpStatus.BAD_REQUEST, 470);
	}
}
