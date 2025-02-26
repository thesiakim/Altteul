package com.c203.altteulbe.user.web.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.user.service.AuthService;
import com.c203.altteulbe.user.web.dto.request.LoginRequestDto;
import com.c203.altteulbe.user.web.dto.request.RegisterUserRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping(value = "/api/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ApiResponseEntity<Void> registerUser(@RequestPart(value="request") RegisterUserRequestDto request,
		@RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {

		authService.registerUser(request, profileImg);

		return ApiResponse.success();
	}

	@PostMapping("/api/login")
	public ApiResponseEntity<Void> login(@RequestBody LoginRequestDto request) {
		return ApiResponse.success();
	}

	@GetMapping("/api/id-check")
	public ApiResponseEntity<Void> validateId(@RequestParam String username) {
		authService.validateId(username);
		return ApiResponse.success();
	}

	@GetMapping("/api/nickname-check")
	public ApiResponseEntity<Void> validateNickname(@RequestParam String nickname,
		@AuthenticationPrincipal Long userId) {
		authService.validateNickname(nickname, userId);
		return ApiResponse.success();
	}

	@GetMapping("/oauth2/authorization/github")
	public ApiResponseEntity<Void> socialLogin() {
		return ApiResponse.success();
	}
}
