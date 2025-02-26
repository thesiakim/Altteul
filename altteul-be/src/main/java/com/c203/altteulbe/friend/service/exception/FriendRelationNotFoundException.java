package com.c203.altteulbe.friend.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class FriendRelationNotFoundException extends BusinessException {
	public FriendRelationNotFoundException() {
		super("친구 관계가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
	}
}
