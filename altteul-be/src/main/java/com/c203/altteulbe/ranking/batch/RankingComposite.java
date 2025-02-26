package com.c203.altteulbe.ranking.batch;

import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RankingComposite {

	private RankingHistory rankingHistory;
	private TodayRanking todayRanking;
}
