package com.c203.altteulbe.game.persistent.repository.side;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.c203.altteulbe.game.persistent.entity.side.SideProblem;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SideProblemRepositoryImpl implements SideProblemCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<SideProblem> findUnsolvedProblemById(long totalCount) {
		return Optional.empty();
	}
}
