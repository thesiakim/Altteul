package com.c203.altteulbe.game.persistent.repository.testcase;

import java.util.List;

import com.c203.altteulbe.game.persistent.entity.problem.Testcase;

public interface TestcaseRepositoryCustom {
	List<Testcase> findTestcasesByProblemId(Long problemId);
}
