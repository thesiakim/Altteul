package com.c203.altteulbe.ranking.web.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.c203.altteulbe.common.response.ApiResponse;
import com.c203.altteulbe.common.response.ApiResponseEntity;
import com.c203.altteulbe.common.response.PageResponse;
import com.c203.altteulbe.common.response.ResponseBody;
import com.c203.altteulbe.ranking.service.TodayRankingService;
import com.c203.altteulbe.ranking.web.response.TodayRankingListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
@Slf4j
public class TodayRankingController {
	private final TodayRankingService rankingService;

	// 랭킹 페이지 전체 조회
	@GetMapping
	public ApiResponseEntity<ResponseBody.Success<PageResponse<TodayRankingListResponseDto>>> getRankingList(
							@PageableDefault(page = 0, size = 10) Pageable pageable,
							@RequestParam(required = false) String nickname,
							@RequestParam(required = false) Long tierId,
							@RequestParam(required = false) String lang) {
		PageResponse<TodayRankingListResponseDto> rankingList =
											rankingService.getRankingList(pageable, nickname, tierId, lang);
		return ApiResponse.success(rankingList, HttpStatus.OK);
	}
}
