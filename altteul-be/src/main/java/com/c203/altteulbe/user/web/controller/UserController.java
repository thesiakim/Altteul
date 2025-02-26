package com.c203.altteulbe.user.web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.user.service.UserService;
import com.c203.altteulbe.user.web.dto.request.UpdateProfileRequestDto;
import com.c203.altteulbe.user.web.dto.response.SearchUserResponseDto;
import com.c203.altteulbe.user.web.dto.response.UserProfileResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ApiResponseEntity<ResponseBody.Success<UserProfileResponseDto>> getUserProfile(
		@PathVariable("userId") Long userId, @AuthenticationPrincipal Long currentUserId) {
		return ApiResponse.success(userService.getUserProfile(userId, currentUserId));
	}

	@PatchMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ApiResponseEntity<Void> updateUserProfile(
		@RequestPart(value = "request") UpdateProfileRequestDto request,
		@RequestPart(required = false, value = "image") MultipartFile image,
		@AuthenticationPrincipal Long currentUserId) {
		userService.updateUserProfile(request, image, currentUserId);
		return ApiResponse.success();
	}

	@GetMapping("/search")
	@PreAuthorize("isAuthenticated()")
	public ApiResponseEntity<ResponseBody.Success<List<SearchUserResponseDto>>> getCurrentUser(
		@AuthenticationPrincipal Long id,
		@RequestParam(value = "nickname") String nickname) {
		List<SearchUserResponseDto> response = userService.searchUser(id, nickname);
		return ApiResponse.success(response, HttpStatus.OK);
	}
}
