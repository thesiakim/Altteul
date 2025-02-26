package com.c203.altteulbe.game.persistent.repository.side;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.side.QSideProblem;
import com.c203.altteulbe.game.persistent.entity.side.QSideProblemHistory;
import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;
import com.c203.altteulbe.user.persistent.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SideProblemHistoryRepositoryImpl implements SideProblemHistoryCustomRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<SideProblemHistory> findByUserIdAndGameIdAndResultWithSideProblem(User user, Game game,
		SideProblemHistory.ProblemResult result) {

		QSideProblemHistory sph = QSideProblemHistory.sideProblemHistory;
		QSideProblem sp = QSideProblem.sideProblem;

		return queryFactory
			.selectFrom(sph)
			.leftJoin(sph.sideProblemId, sp).fetchJoin()
			.where(
				sph.userId.eq(user),
				sph.gameId.eq(game),
				sph.result.eq(result)
			)
			.fetch();
	}

}
