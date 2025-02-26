package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class FriendRequestNotFoundException extends BusinessException {
	public FriendRequestNotFoundException() {
		super("친구 신청을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	}
}
