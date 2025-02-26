package com.c203.altteulbe.ranking.persistent.repository.ranking_history;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;

public interface RankingHistoryRepository extends JpaRepository<RankingHistory, Long>, RankingHistoryRepositoryCustom {
}
