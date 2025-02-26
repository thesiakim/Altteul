package com.c203.altteulbe.common.exception;

import java.security.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class GlobalExceptionHandler {

  /**
   * javax.validation.Valid 또는 @Validated binding error가 발생할 경우
   */
  @ExceptionHandler(BindException.class)
  protected ApiResponseEntity<ResponseBody.Failure> handleBindException(BindException e) {
    log.error("handleBindException", e);
    return ApiResponse.error("유효성 검사 불합격. 자세한 내용은 console 창을 확인해주세요.", HttpStatus.BAD_REQUEST);
  }

  /**
   * 주로 @RequestParam enum으로 binding 못했을 경우 발생
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ApiResponseEntity<ResponseBody.Failure> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    log.error("handleMethodArgumentTypeMismatchException", e);
    return ApiResponse.error("ENUM이랑 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
  }

  /**
   * 지원하지 않은 HTTP method 호출 할 경우 발생
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ApiResponseEntity<ResponseBody.Failure> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    log.error("handleHttpRequestMethodNotSupportedException", e);
    return ApiResponse.error("메서드를 잘못 입력하셨습니다.", HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * Redis 같은 경우 Json 변환을 할 가능성이 높기 때문에 추가
   */
  @ExceptionHandler(JsonProcessingException.class)
  protected ApiResponseEntity<ResponseBody.Failure> JsonProcessingException(
      JsonProcessingException e) {
    log.error("JsonProcessingException", e);
    return ApiResponse.error("전송된 객체를 읽을 수 없습니다.", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ApiResponseEntity<ResponseBody.Failure> HttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    log.error("HttpMessageNotReadableException", e);
    return ApiResponse.error("전송된 문자열을 읽을 수 없습니다.", HttpStatus.BAD_REQUEST);
  }
  /**
   * 비즈니스 로직 실행 중 오류 발생
   */
  @ExceptionHandler(value = {BusinessException.class})
  protected ApiResponseEntity<ResponseBody.Failure> handleConflict(BusinessException e) {
    log.error("BusinessException", e);
    return ApiResponse.error(e.getCode(), e.getMessage(), e.getHttpStatus());
  }

  @ExceptionHandler(SignatureException.class)
  public ApiResponseEntity<ResponseBody.Failure> handleSignatureException() {
    return ApiResponse.error("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ApiResponseEntity<ResponseBody.Failure> handleMalformedJwtException() {
    return ApiResponse.error("올바르지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ApiResponseEntity<ResponseBody.Failure> handleExpiredJwtException() {
    return ApiResponse.error("토큰이 만료되었습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED);
  }


  /**
   * 나머지 예외 발생
   */
  @ExceptionHandler(Exception.class)
  protected ApiResponseEntity<ResponseBody.Failure> handleException(Exception e) {
    log.error("Exception", e);
    return ApiResponse.error("서버 에러입니다. console 창을 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}