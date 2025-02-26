package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class NotAvailableItemException extends BusinessException {
	public NotAvailableItemException() {
		super("사용 불가능한 아이템입니다.", HttpStatus.BAD_REQUEST);
	}
}
