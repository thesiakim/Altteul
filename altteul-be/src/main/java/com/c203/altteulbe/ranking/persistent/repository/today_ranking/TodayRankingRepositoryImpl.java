package com.c203.altteulbe.ranking.persistent.repository.today_ranking;

import static com.c203.altteulbe.game.persistent.entity.QTestHistory.*;
import static com.c203.altteulbe.ranking.persistent.entity.QTier.*;
import static com.c203.altteulbe.ranking.persistent.entity.QTodayRanking.*;
import static com.c203.altteulbe.room.persistent.entity.QTeamRoom.*;
import static com.c203.altteulbe.room.persistent.entity.QUserTeamRoom.*;
import static com.c203.altteulbe.user.persistent.entity.QUser.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.c203.altteulbe.game.persistent.entity.TestHistory;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;
import com.c203.altteulbe.ranking.util.TodayRankingPredicate;
import com.c203.altteulbe.ranking.web.response.TodayRankingListResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;


@Repository
public class TodayRankingRepositoryImpl extends QuerydslRepositorySupport implements TodayRankingRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public TodayRankingRepositoryImpl(JPAQueryFactory queryFactory) {
		super(TodayRanking.class);
		this.queryFactory = queryFactory;
	}

	@Override
	public void deleteByUserId(Long userId) {
		queryFactory
					.delete(todayRanking)
					.where(todayRanking.user.userId.eq(userId))
					.execute();
	}

	// 랭킹 페이지 데이터 반환 시 필요한 평균 통과율 계산
	@Override
	public Page<TodayRankingListResponseDto> findTodayRankingList(Long userId, Pageable pageable,
		String nickname, Long tierId, String lang) {

		JPAQuery<TodayRankingListResponseDto> query = queryFactory
			.select(Projections.constructor(TodayRankingListResponseDto.class,
				user.userId,
				user.nickname,
				tier.id.as("tierId"),
				todayRanking.ranking,
				todayRanking.rankingChange,
				todayRanking.rankingPoint,
				user.mainLang.stringValue(),
				Expressions.numberTemplate(Double.class,
					"IFNULL(({0} * 100.0 / NULLIF({1}, 0)), 0)",  // Pass rate 계산
					testHistoryPassCount(todayRanking.user.userId),       // passCount
					testHistoryTotalCount(todayRanking.user.userId)       // totalCount
				)
			))
			.from(todayRanking)
			.join(todayRanking.user, user)
			.join(user.tier, tier)
			.where(
				TodayRankingPredicate.nicknameContains(nickname),
				TodayRankingPredicate.tierEquals(tierId),
				TodayRankingPredicate.langEquals(lang)
			)
			.orderBy(
				Expressions.numberTemplate(Integer.class,
					"CASE WHEN {0} = {1} THEN 1 ELSE 2 END",  // 로그인한 사용자는 최상단 정렬
					todayRanking.user.userId, userId
				).asc(),
				todayRanking.ranking.asc()   // 기본 랭킹 정렬
			);

		// 전체 개수 조회
		JPAQuery<Long> countQuery = queryFactory
			.select(todayRanking.count())
			.from(todayRanking)
			.where(
				TodayRankingPredicate.nicknameContains(nickname),
				TodayRankingPredicate.tierEquals(tierId),
				TodayRankingPredicate.langEquals(lang)
			);

		// 페이징 적용 후 결과 조회
		List<TodayRankingListResponseDto> rankingList = getQuerydsl()
									.applyPagination(pageable, query)
									.fetch();

		return PageableExecutionUtils.getPage(rankingList, pageable, countQuery::fetchOne);
	}

	// 특정 유저와 팀원들의 통과 횟수 계산 (result = P 인 경우)
	private JPQLQuery<Long> testHistoryPassCount(NumberExpression<Long> userId) {
		return JPAExpressions.select(testHistory.count())
			.from(testHistory)
			.where(testHistory.result.eq(TestHistory.Status.P)
				.and(testHistory.user.userId.in(
					JPAExpressions.select(userTeamRoom.user.userId)
						.from(userTeamRoom)
						.join(userTeamRoom.teamRoom, teamRoom)
						.where(userTeamRoom.teamRoom.in(
							JPAExpressions.select(userTeamRoom.teamRoom)
								.from(userTeamRoom)
								.where(userTeamRoom.user.userId.eq(userId))
						))
				))
			);
	}

	// 특정 유저와 팀원들의 전체 제출 횟수 계산
	private JPQLQuery<Long> testHistoryTotalCount(NumberExpression<Long> userId) {
		return JPAExpressions.select(testHistory.count())
			.from(testHistory)
			.where(testHistory.user.userId.in(
				JPAExpressions.select(userTeamRoom.user.userId)
					.from(userTeamRoom)
					.join(userTeamRoom.teamRoom, teamRoom)
					.where(userTeamRoom.teamRoom.in(
						JPAExpressions.select(userTeamRoom.teamRoom)
							.from(userTeamRoom)
							.where(userTeamRoom.user.userId.eq(userId))
					))
			));
	}
}
