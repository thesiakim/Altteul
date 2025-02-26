package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class InvalidFriendRequestException extends BusinessException {
	public InvalidFriendRequestException() {
		super("자기 자신에게 친구 신청할 수 없습니다.", HttpStatus.BAD_REQUEST);
	}
}
