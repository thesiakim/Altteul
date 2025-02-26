package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class MatchingProcessException extends BusinessException {
	public MatchingProcessException() {
		super("매칭 중 문제가 발생했습니다.", HttpStatus.NOT_IMPLEMENTED, 458);
	}
}
