package com.c203.altteulbe.game.persistent.repository.problem;

import java.util.List;
import java.util.Optional;

import com.c203.altteulbe.common.dto.Language;
import com.c203.altteulbe.game.persistent.entity.problem.Problem;

public interface ProblemRepositoryCustom {
	List<Long> findAllProblemIds();
	Optional<Problem> findWithLangByProblemIdAndLang(Long problemId, Language language);

	Optional<Problem> findWithExamplesByProblemId(Long problemId);
}
