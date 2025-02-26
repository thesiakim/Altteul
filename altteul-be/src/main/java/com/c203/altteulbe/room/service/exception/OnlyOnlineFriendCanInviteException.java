package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class OnlyOnlineFriendCanInviteException extends BusinessException {
	public OnlyOnlineFriendCanInviteException() {
		super("접속 중인 친구만 초대할 수 있습니다.", HttpStatus.BAD_REQUEST, 461);
	}
}
