package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class RedisConnectionFailException extends BusinessException {
	public RedisConnectionFailException() {
		super("Redis 연결에 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
