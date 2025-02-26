package com.c203.altteulbe.game.persistent.repository.side;

import java.util.List;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;
import com.c203.altteulbe.user.persistent.entity.User;

public interface SideProblemHistoryCustomRepository {
	List<SideProblemHistory> findByUserIdAndGameIdAndResultWithSideProblem(User user, Game game,
		SideProblemHistory.ProblemResult result);
}
