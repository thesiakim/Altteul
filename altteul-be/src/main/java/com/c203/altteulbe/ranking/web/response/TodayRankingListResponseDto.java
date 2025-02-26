package com.c203.altteulbe.ranking.web.response;

import com.c203.altteulbe.common.dto.Language;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodayRankingListResponseDto {
	private Long userId;
	private String nickname;    // User.nickname
	private Long tierId;        // User.Tier.tierId
	private int ranking;        // TodayRanking.ranking
	private Long rankChange;    // TodayRanking.rankingChange
	private Long point;         // TodayRanking.rankingPoint
	private String lang;        // User.mainlang
	private double rate;        // 승률
}
