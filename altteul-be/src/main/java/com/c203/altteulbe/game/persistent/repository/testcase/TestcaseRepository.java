package com.c203.altteulbe.game.persistent.repository.testcase;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.problem.Testcase;

public interface TestcaseRepository extends JpaRepository<Testcase, Long>, TestcaseRepositoryCustom {
}
