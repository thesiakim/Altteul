package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class ItemNotFoundException extends BusinessException {
	public ItemNotFoundException() { super("아이템을 찾을 수 없습니다.", HttpStatus.NOT_FOUND); }
}