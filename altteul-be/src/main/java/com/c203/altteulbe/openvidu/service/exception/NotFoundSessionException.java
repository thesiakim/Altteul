package com.c203.altteulbe.openvidu.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotFoundSessionException extends BusinessException {
	public NotFoundSessionException() {
		super("음성 채팅 세션이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
	}
}
