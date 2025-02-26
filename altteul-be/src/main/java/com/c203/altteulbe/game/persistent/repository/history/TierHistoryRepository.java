package com.c203.altteulbe.game.persistent.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.ranking.persistent.entity.TierHistory;

public interface TierHistoryRepository extends JpaRepository<TierHistory, Long> {
}
