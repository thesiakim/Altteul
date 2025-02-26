package com.c203.altteulbe.game.persistent.repository.game;

import static com.c203.altteulbe.game.persistent.entity.QGame.*;
import static com.c203.altteulbe.game.persistent.entity.item.QItemHistory.*;
import static com.c203.altteulbe.ranking.persistent.entity.QTier.*;
import static com.c203.altteulbe.ranking.persistent.entity.QTodayRanking.*;
import static com.c203.altteulbe.room.persistent.entity.QSingleRoom.*;
import static com.c203.altteulbe.room.persistent.entity.QTeamRoom.*;
import static com.c203.altteulbe.room.persistent.entity.QUserTeamRoom.*;
import static com.c203.altteulbe.user.persistent.entity.QUser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.problem.QProblem;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class GameRepositoryImpl extends QuerydslRepositorySupport implements GameCustomRepository {
	private final JPAQueryFactory queryFactory;

	public GameRepositoryImpl(JPAQueryFactory queryFactory) {
		super(Game.class);
		this.queryFactory = queryFactory;
	}

	@Override
	public List<Game> findWithItemAndProblemAndAllMemberByUserId(Long userId) {
		QProblem problem = QProblem.problem;
		JPAQuery<Game> teamRoomQuery = queryFactory
			.selectFrom(game)
			.leftJoin(game.teamRooms, teamRoom)
			.leftJoin(game.itemHistories, itemHistory).distinct()
			.leftJoin(teamRoom.userTeamRooms, userTeamRoom)
			.leftJoin(userTeamRoom.user, user)
			.leftJoin(game.problem, problem)
			.leftJoin(user.tier, tier)
			.leftJoin(user.todayRanking, todayRanking)
			.where(game.in(  // `game`을 기준으로 `userId`에 해당하는 `teamRoom`을 찾음
				JPAExpressions
					.select(teamRoom.game)  // teamRoom과 관련된 game을 가져옴
					.from(userTeamRoom)
					.join(userTeamRoom.teamRoom, teamRoom)  // `userTeamRoom`을 통해 `teamRoom`을 가져옴
					.where(userTeamRoom.user.userId.eq(userId).and(teamRoom.activation.eq(false)))
				).and(game.completedAt.isNotNull())
			);

		JPAQuery<Game> singleRoomQuery = queryFactory
			.selectFrom(game)
			.leftJoin(game.singleRooms, singleRoom).fetchJoin()
			.leftJoin(game.problem, problem)
			.leftJoin(singleRoom.user, user).fetchJoin()
			.leftJoin(user.tier, tier).fetchJoin()
			.leftJoin(user.todayRanking, todayRanking).fetchJoin()
			.where(game.in(
				JPAExpressions
					.select(singleRoom.game)
					.from(singleRoom)
					.where(singleRoom.user.userId.eq(userId).and(singleRoom.activation.eq(false)))
			).and(game.completedAt.isNotNull())
		);

		List<Game> teamRoomGames = teamRoomQuery.fetch();
		List<Game> singleRoomGames = singleRoomQuery.fetch();

		List<Game> result = new ArrayList<>();
		result.addAll(teamRoomGames);
		result.addAll(singleRoomGames);

		return result;
	}

	@Override
	public Optional<Game> findWithAllMemberByGameId(Long gameId) {
		BattleType gameType = queryFactory
			.select(game.battleType)
			.from(game)
			.where(game.id.eq(gameId))
			.fetchOne();

		if (BattleType.S.equals(gameType)) {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.singleRooms, singleRoom).fetchJoin()
				.leftJoin(user.tier, tier).fetchJoin()
				.leftJoin(singleRoom.user, user).fetchJoin()
				.where(game.id.eq(gameId))
				.fetchOne()
			);
		} else {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.teamRooms, teamRoom).fetchJoin()
				.leftJoin(teamRoom.userTeamRooms, userTeamRoom)
				.leftJoin(userTeamRoom.user, user).fetchJoin()
				.leftJoin(user.tier, tier).fetchJoin()
				.where(game.id.eq(gameId))
				.fetchOne()
			);
		}

	}

	@Override
	public Optional<Game> findWithRoomByGameId(Long gameId) {
		return Optional.ofNullable(queryFactory
			.selectFrom(game)
			.where(game.id.eq(gameId))
			.leftJoin(game.teamRooms, teamRoom)
			.leftJoin(game.singleRooms, singleRoom)
			.fetchOne()
		);
	}

	@Override
	public Optional<Game> findWithRoomAndProblemByGameIdAndTeamId(Long gameId, Long teamId) {
		QProblem problem = QProblem.problem;
		BattleType gameType = queryFactory
			.select(game.battleType)
			.from(game)
			.where(game.id.eq(gameId))
			.fetchOne();

		if (BattleType.S.equals(gameType)) {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.singleRooms, singleRoom).fetchJoin()
				.leftJoin(game.problem, problem).fetchJoin()
				.where(game.id.eq(gameId).and(singleRoom.id.eq(teamId)))
				.fetchOne()
			);
		} else {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.teamRooms, teamRoom).fetchJoin()
				.leftJoin(game.problem, problem).fetchJoin()
				.where(game.id.eq(gameId).and(teamRoom.id.eq(teamId)))
				.fetchOne()
			);
		}
	}

	public Optional<Game> findWithGameByRoomIdAndType(Long roomId, BattleType battleType) {
		if (BattleType.T.equals(battleType)) {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.teamRooms, teamRoom).fetchJoin()
				.leftJoin(game.problem).fetchJoin()
				.where(teamRoom.id.eq(roomId))
				.fetchOne()
			);
		} else {
			return Optional.ofNullable(queryFactory
				.selectFrom(game)
				.leftJoin(game.singleRooms, singleRoom).fetchJoin()
				.leftJoin(game.problem).fetchJoin()
				.where(singleRoom.id.eq(roomId))
				.fetchOne()
			);
		}
	}
}
