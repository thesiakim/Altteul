package com.c203.altteulbe.room.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.room.service.TeamRoomService;
import com.c203.altteulbe.room.web.dto.request.InviteTeamRequestDto;
import com.c203.altteulbe.room.web.dto.request.InviteTeamAnswerRequestDto;
import com.c203.altteulbe.room.web.dto.response.RoomEnterResponseDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamRoomController {

	private final TeamRoomService teamRoomService;

	/*
	 * 팀전 방 입장 API
	 */
	@PostMapping("/enter")
	public ApiResponseEntity<ResponseBody.Success<RoomEnterResponseDto>> enterTeamRoom(
		@AuthenticationPrincipal Long userId) {

		RoomEnterResponseDto responseDto = teamRoomService.enterTeamRoom(userId);
		return ApiResponse.success(responseDto, HttpStatus.OK);
	}

	/*
	 * 팀전 방 퇴장 API
	 */
	@PostMapping("/leave/{roomId}")
	public ApiResponseEntity<Void> leaveTeamRoom(@PathVariable(value = "roomId") Long roomId,
												 @AuthenticationPrincipal Long userId) {
		teamRoomService.leaveTeamRoom(roomId, userId);
		return ApiResponse.success();
	}

	/*
	 * 팀전 매칭 API
	 */
	@PostMapping("/matching/{roomId}")
	public ApiResponseEntity<Void> startTeamMatch(@PathVariable(value = "roomId") Long roomId,
												  @AuthenticationPrincipal Long userId) {
		teamRoomService.startTeamMatch(roomId, userId);
		return ApiResponse.success();
	}

	/*
	 * 팀전 매칭 취소 API
	 */
	@PostMapping("/matching/cancel/{roomId}")
	public ApiResponseEntity<Void> cancelTeamMatch(@PathVariable(value = "roomId") Long roomId,
												   @AuthenticationPrincipal Long userId) {
		teamRoomService.cancelTeamMatch(roomId, userId);
		return ApiResponse.success();
	}

	/*
	 * 팀전 초대 API
	 */
	@PostMapping("/invite")
	public ApiResponseEntity<Void> inviteFriendToTeam(@RequestBody InviteTeamRequestDto requestDto,
													  @AuthenticationPrincipal Long userId) {
		teamRoomService.inviteFriendToTeam(requestDto, userId);
		return ApiResponse.success();
	}

	/*
	 * 팀전 초대 수락 및 거절 API
	 */
	@PostMapping("/invite/reaction")
	public ApiResponseEntity<ResponseBody.Success<RoomEnterResponseDto>> handleInviteReaction(
														@RequestBody InviteTeamAnswerRequestDto requestDto,
													    @AuthenticationPrincipal Long userId) {
		RoomEnterResponseDto responseDto = teamRoomService.handleInviteReaction(requestDto, userId);
		if (responseDto == null) {
			return ApiResponse.success(null, HttpStatus.NO_CONTENT, "초대가 거절되었습니다.");
		}
		return ApiResponse.success(responseDto, HttpStatus.OK);
	}
}

