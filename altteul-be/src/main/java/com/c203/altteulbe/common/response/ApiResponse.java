package com.c203.altteulbe.common.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ApiResponse<F> {

	//status 자동 ok
	public static <B> ApiResponseEntity<ResponseBody.Success<B>> success(B body) {
		return new ApiResponseEntity<>(new ResponseBody.Success<>(body, "OK", HttpStatus.OK.value()), HttpStatus.OK);
	}
	public static <B> ApiResponseEntity<ResponseBody.Success<B>> success(B body, HttpStatus status, String message) {
		return new ApiResponseEntity<>(new ResponseBody.Success<>(body, message, status.value()), status);
	}
	public static <B> ApiResponseEntity<ResponseBody.Success<B>> success(B body, HttpStatus status) {
		return new ApiResponseEntity<>(new ResponseBody.Success<>(body, "OK", status.value()), status);
	}
	// 헤더 포함
	public static <B> ApiResponseEntity<ResponseBody.Success<B>> success(B body, MultiValueMap<String, String> headers, HttpStatus status, String message) {
		return new ApiResponseEntity<>(new ResponseBody.Success<>(body, message, status.value()), headers, status);
	}
	public static <B> ApiResponseEntity<ResponseBody.Success<B>> success(B body, MultiValueMap<String, String> headers, HttpStatus status) {
		return new ApiResponseEntity<>(new ResponseBody.Success<>(body, "OK", status.value()), headers, status);
	}
	// 데이터 없음
	public static ApiResponseEntity<Void> success() {
		return new ApiResponseEntity<>(HttpStatus.OK);
	}

	public static ApiResponseEntity<ResponseBody.Failure> error(
		final ResponseBody.Failure body, final HttpStatus status) {
		return new ApiResponseEntity<>(body, status);
	}

	public static ApiResponseEntity<ResponseBody.Failure> error(
		final String message, final HttpStatus status) {
		return new ApiResponseEntity<>(
			new ResponseBody.Failure(status.value(), message), status);
	}

	public static ApiResponseEntity<ResponseBody.Failure> error(
		final int code, final String message, final HttpStatus status) {
		return new ApiResponseEntity<>(new ResponseBody.Failure(code, message), status);
	}

	public static ApiResponseEntity<ResponseBody.Failure> error(
		BindingResult bindingResult, final HttpStatus status) {
		return new ApiResponseEntity<>(
			new ResponseBody.Failure(
				status.value(), createErrorMessage(bindingResult)),
			status);
	}

	/*
	 * Binding 오류 발생 시에 에러 메세지 형식을 지정함
	 */
	private static String createErrorMessage(BindingResult bindingResult) {
		StringBuilder sb = new StringBuilder();

		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			sb.append("- Field: '").append(fieldError.getField()).append("'\n");
			sb.append("  Message: ").append(fieldError.getDefaultMessage()).append("\n");
		}

		return sb.toString().trim(); // 마지막 개행 제거
	}


	private MultiValueMap<String, String> setCookie(String cookieValue) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("Set-Cookie", cookieValue);

		return map;
	}
}