package com.c203.altteulbe.ranking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.c203.altteulbe.common.response.PageResponse;
import com.c203.altteulbe.ranking.persistent.repository.today_ranking.TodayRankingRepository;
import com.c203.altteulbe.ranking.web.response.TodayRankingListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodayRankingService {

	private final TodayRankingRepository todayRankingRepository;

	public PageResponse<TodayRankingListResponseDto> getRankingList(Pageable pageable, String nickname,
																	Long tireId, String lang) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = null;

		// 비로그인 사용자
		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
			log.info("비로그인한 유저의 랭킹 페이지 조회");
		// 로그인 사용자
		} else {
			userId = (Long) authentication.getPrincipal();
			log.info("로그인한 유저의 랭킹 페이지 조회, userId={}", userId);
		}
		Page<TodayRankingListResponseDto> todayRankingList = todayRankingRepository.findTodayRankingList(
								userId, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
								nickname, tireId, lang);
		return new PageResponse<>("rankings", todayRankingList);
	}
}
