package com.c203.altteulbe.user.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class DuplicateNicknameException extends BusinessException {
	public DuplicateNicknameException() {
		super("이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST);
	}
}
