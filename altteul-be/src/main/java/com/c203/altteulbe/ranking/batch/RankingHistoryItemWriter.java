package com.c203.altteulbe.ranking.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;
import com.c203.altteulbe.ranking.persistent.repository.ranking_history.RankingHistoryRepository;
import com.c203.altteulbe.ranking.persistent.repository.today_ranking.TodayRankingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankingHistoryItemWriter implements ItemWriter<RankingComposite> {

	private final RankingHistoryRepository rankingHistoryRepository;
	private final TodayRankingRepository todayRankingRepository;

	@Override
	public void write(Chunk<? extends RankingComposite> chunk) throws Exception {
		List<RankingHistory> histories = new ArrayList<>();
		List<TodayRanking> rankings = new ArrayList<>();

		for(RankingComposite composite : chunk.getItems()) {
			histories.add(composite.getRankingHistory());
			rankings.add(composite.getTodayRanking());
		}
		rankingHistoryRepository.saveAll(histories);
		todayRankingRepository.saveAll(rankings);
	}
}
