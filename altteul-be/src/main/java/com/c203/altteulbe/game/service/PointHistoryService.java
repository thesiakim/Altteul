package com.c203.altteulbe.game.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c203.altteulbe.game.persistent.entity.PointHistory;
import com.c203.altteulbe.game.persistent.repository.history.PointHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PointHistoryService {
	private final ApplicationEventPublisher eventPublisher;
	private final PointHistoryRepository pointHistoryRepository;

	/*
	 * 이후 PointHistory 저장 구현 시 매개변수의 PointHistory는 제거하고, 자유롭게 작성하시면 됩니다
	 * DB에 저장되는 PointHistory를 이벤트 발행할 때 넘겨주시기만 하면 돼요
	 */
	public void savePointHistory(PointHistory pointHistory) {
		// PointHistory를 DB에 저장할 때 Tier 업데이트 이벤트 발생
		pointHistoryRepository.save(pointHistory);
		eventPublisher.publishEvent(new PointHistorySavedEvent(pointHistory));
	}

	public void savePointHistory(List<PointHistory> pointHistories) {
		// PointHistory를 DB에 저장할 때 Tier 업데이트 이벤트 발생

		pointHistoryRepository.saveAll(pointHistories);
		for(PointHistory pointHistory : pointHistories) {
			eventPublisher.publishEvent(new PointHistorySavedEvent(pointHistory));
		}
	}
}
