package com.c203.altteulbe.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * BusinessException을 상속받아 여러 Exception 생성 가능. 기능을 그대로 유지할 수 있음
 */
@Getter
public class BusinessException extends RuntimeException {
	private final HttpStatus httpStatus;
	private final int code;

	public BusinessException(String message, HttpStatus httpStatus) {
		super(message);
		this.code = httpStatus.value();
		this.httpStatus = httpStatus;
	}

	public BusinessException(String message, HttpStatus httpStatus, int code) {
		super(message);
		this.code = code;
		this.httpStatus = httpStatus;
	}
}