package com.c203.altteulbe.game.persistent.repository.history;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.TestHistory;

public interface TestHistoryRepository extends JpaRepository<TestHistory, Long> {
}
