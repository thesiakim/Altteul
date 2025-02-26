package com.c203.altteulbe.game.persistent.repository.testcase;

import static com.c203.altteulbe.game.persistent.entity.problem.QTestcase.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.c203.altteulbe.game.persistent.entity.problem.Testcase;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TestcaseRepositoryImpl implements TestcaseRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Testcase> findTestcasesByProblemId(Long problemId) {
		return queryFactory
			.selectFrom(testcase)
			.where(testcase.problem.id.eq(problemId)
				   .and(testcase.number.in(1,2)))
			.fetch();
	}
}
