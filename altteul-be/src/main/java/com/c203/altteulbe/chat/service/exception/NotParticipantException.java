package com.c203.altteulbe.chat.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotParticipantException extends BusinessException {
	public NotParticipantException() {
		super("채팅방 참여자가 아닙니다.", HttpStatus.FORBIDDEN);
	}
}
