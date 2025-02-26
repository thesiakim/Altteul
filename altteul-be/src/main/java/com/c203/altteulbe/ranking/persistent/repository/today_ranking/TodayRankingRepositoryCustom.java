package com.c203.altteulbe.ranking.persistent.repository.today_ranking;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.c203.altteulbe.ranking.web.response.TodayRankingListResponseDto;

public interface TodayRankingRepositoryCustom {

	void deleteByUserId(Long userId);
	Page<TodayRankingListResponseDto> findTodayRankingList(Long userId, Pageable pageable,
		String nickname, Long tireId, String lang);
}