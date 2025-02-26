package com.c203.altteulbe.ranking.persistent.repository.ranking_history;

import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;

public interface RankingHistoryRepositoryCustom {

	RankingHistory findLatestByUser(Long userId);
}
