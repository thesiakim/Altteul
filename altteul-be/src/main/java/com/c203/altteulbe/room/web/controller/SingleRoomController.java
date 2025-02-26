package com.c203.altteulbe.room.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.room.service.SingleRoomService;
import com.c203.altteulbe.room.web.dto.response.RoomEnterResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/single")
public class SingleRoomController {

	private final SingleRoomService singleRoomService;

	/*
	 * 개인전 방 입장 API
	 */
	@PostMapping("/enter")
	public ApiResponseEntity<ResponseBody.Success<RoomEnterResponseDto>> enterSingleRoom(
														@AuthenticationPrincipal Long userId) {
		RoomEnterResponseDto responseDto = singleRoomService.enterSingleRoom(userId);
		return ApiResponse.success(responseDto, HttpStatus.OK);
	}

	/*
	 * 개인전 방 퇴장 API
	 */
	@PostMapping("/leave/{roomId}")
	public ApiResponseEntity<Void> leaveSingleRoom(@PathVariable(value = "roomId") Long roomId,
												   @AuthenticationPrincipal Long userId) {
		singleRoomService.leaveSingleRoom(roomId, userId);
		return ApiResponse.success();
	}

	/*
	 * 개인전 게임 시작 API
	 */
	@PostMapping("/start/{roomId}")
	public ApiResponseEntity<Void> startGame(@PathVariable(value = "roomId") Long roomId,
										     @AuthenticationPrincipal Long leaderId) {
		singleRoomService.startGame(roomId, leaderId);
		return ApiResponse.success();
	}
}
