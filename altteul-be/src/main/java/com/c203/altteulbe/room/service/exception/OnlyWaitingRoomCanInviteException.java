package com.c203.altteulbe.room.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class OnlyWaitingRoomCanInviteException extends BusinessException {
	public OnlyWaitingRoomCanInviteException() {
		super("대기 중일 때만 초대할 수 있습니다.", HttpStatus.BAD_REQUEST, 462);
	}
}
