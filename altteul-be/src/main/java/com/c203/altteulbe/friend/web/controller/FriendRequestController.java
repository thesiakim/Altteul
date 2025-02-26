package com.c203.altteulbe.friend.web.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c203.altteulbe.common.dto.RequestStatus;
import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.PageResponse;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.friend.service.FriendRequestService;
import com.c203.altteulbe.friend.service.FriendWSService;
import com.c203.altteulbe.friend.web.dto.request.CreateFriendRequestDto;
import com.c203.altteulbe.friend.web.dto.request.ProcessFriendRequestDto;
import com.c203.altteulbe.friend.web.dto.response.FriendRequestResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class FriendRequestController {
	private final FriendRequestService friendRequestService;
	private final FriendWSService friendWSService;

	// 친구 요청 리스트 조회
	@GetMapping("/friend/request")
	public ApiResponseEntity<ResponseBody.Success<PageResponse<FriendRequestResponseDto>>> getFriendRequestList(
		@AuthenticationPrincipal Long id,
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) throws JsonProcessingException {
		log.info("친구 요청 리스트 조회 시작");
		PageResponse<FriendRequestResponseDto> friendRequest = friendRequestService.getPendingRequestsFromRedis(id,
			pageable);
		return ApiResponse.success(friendRequest, HttpStatus.OK);
	}

	// 친구 신청
	@PostMapping("/friend/request")
	public ApiResponseEntity<Void> handleFriendRequest(@RequestBody CreateFriendRequestDto request,
		@AuthenticationPrincipal Long userId) throws
		JsonProcessingException {
		log.info("유저 {}로부터 유저 {}가 친구 요청 받기", userId, request.getToUserId()); // 에게 -> 가
		Long toUserId = request.getToUserId();
		FriendRequestResponseDto result = friendRequestService.createFriendRequest(userId, toUserId);

		friendWSService.sendRequestMessage(toUserId, result);
		log.info("유저 {}에게 친구 요청 보내기", request.getToUserId());
		return ApiResponse.success();
	}

	// 친구 요청 처리
	@PostMapping("/friend/request/process")
	public ApiResponseEntity<Void> handleRequestProcess(
		@RequestBody ProcessFriendRequestDto request,
		@AuthenticationPrincipal Long userId
	) throws JsonProcessingException {
		log.info("유저 {}가 받은 친구 요청 처리", request.getToUserId());
		Long requestId = request.getFriendRequestId();
		RequestStatus status = request.getRequestStatus();
		friendRequestService.processRequest(requestId, userId, status);
		log.info("친구 신청이 {} 되었습니다.", status == RequestStatus.A ? "수락" : "거절");
		// 친구 신청을 수락했을 경우
		if (status == RequestStatus.A) {
			// 클라이언트에게 업데이트가 필요하다고 알림
			log.info("현재 로그인한 유저 id: {}", userId); // 현재 로그인한 유저의 id
			log.info("친구 요청을 보낸 유저의 id: {}", request.getFromUserId()); // 친구 요청을 보낸 유저의 id
			friendWSService.sendFriendListUpdateMessage(userId);
			friendWSService.sendFriendListUpdateMessage(request.getFromUserId());
		}
		return ApiResponse.success();
	}

}
