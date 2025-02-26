package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class UserStatusUpdateFailException extends BusinessException {
	public UserStatusUpdateFailException() {
		super("사용자 상태 업데이트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
