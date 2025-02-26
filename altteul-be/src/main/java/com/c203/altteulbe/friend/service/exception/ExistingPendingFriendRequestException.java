package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class ExistingPendingFriendRequestException extends BusinessException {
	public ExistingPendingFriendRequestException() {
		super("이미 보류 중인 친구 신청이 있습니다.", HttpStatus.BAD_REQUEST);
	}
}
