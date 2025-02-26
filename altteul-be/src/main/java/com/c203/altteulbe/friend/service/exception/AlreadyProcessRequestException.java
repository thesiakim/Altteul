package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AlreadyProcessRequestException extends BusinessException {
	public AlreadyProcessRequestException() {
		super("이미 처리된 친구 신청입니다.", HttpStatus.BAD_REQUEST);
	}
}
