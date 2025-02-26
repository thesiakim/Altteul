package com.c203.altteulbe.ranking.batch;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;
import com.c203.altteulbe.ranking.persistent.repository.ranking_history.RankingHistoryRepository;
import com.c203.altteulbe.ranking.persistent.repository.today_ranking.TodayRankingRepository;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RankingHistoryItemProcessor implements ItemProcessor<User, RankingComposite> {

	private final RankingHistoryRepository rankingHistoryRepository;
	private final UserRepository userRepository;
	private final TodayRankingRepository todayRankingRepository;

	public RankingHistoryItemProcessor(
										RankingHistoryRepository rankingHistoryRepository,
										TodayRankingRepository todayRankingRepository,
										UserRepository userRepository) {
		this.rankingHistoryRepository = rankingHistoryRepository;
		this.userRepository = userRepository;
		this.todayRankingRepository = todayRankingRepository;
	}

	@Override
	public RankingComposite process(User user) {

		// TodayRanking에서 전날 데이터 삭제
		todayRankingRepository.deleteByUserId(user.getUserId());

		// 전날 랭킹 정보 조회
		RankingHistory yesterdayRanking = rankingHistoryRepository.findLatestByUser(user.getUserId());

		List<User> rankedUsers = userRepository.findAllOrderedByRankingPointTierUsername();

		// 랭킹 계산
		int currentRanking = 1;
		for (User rankedUser : rankedUsers) {
			if (rankedUser.getUserId().equals(user.getUserId())) {
				break;
			}
			currentRanking++;
		}

		// 변동된 순위
		int rankingChange;

		// 처음 가입한 유저의 경우 이전 랭킹이 없으므로 계산된 랭킹 그대로 저장
		if (yesterdayRanking == null) {
			rankingChange = currentRanking;
		} else {
			// 이전 랭킹 기록이 있는 유저의 경우 (어제 랭킹 - 현재 랭킹)
			rankingChange = yesterdayRanking.getRanking()-currentRanking;
		}

		RankingHistory rankingHistory = RankingHistory.create(
															user,
															user.getTier(),
															user.getRankingPoint(),
															currentRanking,
															rankingChange
														);
		TodayRanking todayRanking = TodayRanking.create(
														user,
														user.getTier(),
														user.getRankingPoint(),
														(long) rankingChange,
														currentRanking
													);
		return new RankingComposite(rankingHistory, todayRanking);
	}
}

