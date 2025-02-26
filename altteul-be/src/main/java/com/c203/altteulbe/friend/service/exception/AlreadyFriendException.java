package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class AlreadyFriendException extends BusinessException {
	public AlreadyFriendException() {
		super("이미 친구입니다.", HttpStatus.BAD_REQUEST);
	}
}
