package com.c203.altteulbe.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import lombok.Getter;

@Getter
public class ApiResponseEntity<B> extends ResponseEntity<B> {
	public ApiResponseEntity(final HttpStatus status) {
		super(status);
	}

	public ApiResponseEntity(final B body, final HttpStatus status) {
		super(body, status);
	}

	public ApiResponseEntity(final B body, MultiValueMap<String, String> headers, HttpStatus status) {
		super(body, headers, status);
	}
}
