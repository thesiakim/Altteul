package com.c203.altteulbe.ranking.persistent.repository.ranking_history;

import org.springframework.stereotype.Repository;
import static com.c203.altteulbe.ranking.persistent.entity.QRankingHistory.*;
import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingHistoryRepositoryImpl implements RankingHistoryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	// 특정 유저의 가장 최신 기록 조회
	@Override
	public RankingHistory findLatestByUser(Long userId) {
		return queryFactory
						.selectFrom(rankingHistory)
						.where(rankingHistory.user.userId.eq(userId))
						.orderBy(rankingHistory.createdAt.desc())   // 최신 기록부터 정렬
						.limit(1)
						.fetchOne();
	}
}
