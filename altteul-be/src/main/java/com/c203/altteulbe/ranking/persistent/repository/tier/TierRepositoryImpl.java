package com.c203.altteulbe.ranking.persistent.repository.tier;

import static com.c203.altteulbe.ranking.persistent.entity.QTier.*;

import org.springframework.stereotype.Repository;

import com.c203.altteulbe.ranking.persistent.entity.Tier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TierRepositoryImpl implements TierRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	// 현재 포인트가 속한 티어 반환
	@Override
	public Tier findTierByPoint(Long point) {
		// minPoint <= point <= maxPoint
		return queryFactory
			.selectFrom(tier)
			.where(tier.minPoint.loe(point)
				.and(tier.maxPoint.goe(point)))
			.fetchOne();
	}

	// 최고 포인트에 해당하는 티어 반환
	@Override
	public Tier getHighestTier() {
		return queryFactory
			.selectFrom(tier)
			.orderBy(tier.maxPoint.desc())  // 가장 높은 maxPoint를 가진 티어
			.fetchFirst();
	}
}
