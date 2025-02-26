package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class OnlyFriendCanInviteException extends BusinessException {
	public OnlyFriendCanInviteException() {
		super("친구만 초대할 수 있습니다.", HttpStatus.BAD_REQUEST, 460);
	}
}
