package com.c203.altteulbe.game.web.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.PageResponse;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.game.service.GameHistoryService;
import com.c203.altteulbe.game.service.GameLeaveService;
import com.c203.altteulbe.game.service.result.AIFeedbackService;
import com.c203.altteulbe.game.service.result.GameResultService;
import com.c203.altteulbe.game.web.dto.leave.request.GameLeaveRequestDto;
import com.c203.altteulbe.game.web.dto.record.response.GameRecordResponseDto;
import com.c203.altteulbe.game.web.dto.result.request.AIFeedbackRequestDto;
import com.c203.altteulbe.game.web.dto.result.response.AIFeedbackResponse;
import com.c203.altteulbe.game.web.dto.result.response.GameResultResponseDto;
import com.c203.altteulbe.game.web.dto.result.response.OpponentCodeResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GameController {

	private final GameResultService gameResultService;
	private final GameHistoryService gameHistoryService;
	private final GameLeaveService gameLeaveService;
	private final AIFeedbackService aiFeedbackService;

	@GetMapping("/game/history/{userId}")
	public ApiResponseEntity<ResponseBody.Success<PageResponse<GameRecordResponseDto>>> getGameRecord(
		@PathVariable Long userId,
		@PageableDefault(page = 0, size = 10) Pageable pageable) {

		return ApiResponse.success(gameHistoryService.getGameRecord(userId, pageable));
	}

	@GetMapping("/game/{gameId}/result")
	public ApiResponseEntity<ResponseBody.Success<GameResultResponseDto>> getGameResult(@PathVariable Long gameId,
		@AuthenticationPrincipal Long userId) {
		return ApiResponse.success(gameResultService.getGameResult(gameId, userId));
	}

	@GetMapping("/game/result/feedback")
	public ApiResponseEntity<ResponseBody.Success<AIFeedbackResponse>> getCodeEvaluation(
		@ModelAttribute AIFeedbackRequestDto request) {
		return ApiResponse.success(aiFeedbackService.getEvaluation(request));
	}

	@PostMapping("/game/leave")
	public ApiResponseEntity<Void> leaveGame(@AuthenticationPrincipal Long userId,
		@RequestBody GameLeaveRequestDto request) {
		gameLeaveService.leaveGame(userId, request);
		return ApiResponse.success();
	}

	@GetMapping("game/code/{roomId}")
	public ApiResponseEntity<ResponseBody.Success<OpponentCodeResponseDto>> getOpponentCode(
		@PathVariable(value = "roomId") Long roomId,
		@RequestParam("gameId") Long gameId,
		@RequestParam("type") BattleType type) {
		OpponentCodeResponseDto response = gameResultService.getOpponentCode(roomId, gameId, type);
		return ApiResponse.success(response, HttpStatus.OK);
	}
}
