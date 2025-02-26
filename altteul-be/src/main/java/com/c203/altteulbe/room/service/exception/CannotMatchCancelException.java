package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class CannotMatchCancelException extends BusinessException {
	public CannotMatchCancelException() {
		super("이미 매칭된 팀이 있어 취소할 수 없습니다.", HttpStatus.BAD_REQUEST, 455);
	}
}
