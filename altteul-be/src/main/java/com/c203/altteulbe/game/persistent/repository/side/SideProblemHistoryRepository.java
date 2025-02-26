package com.c203.altteulbe.game.persistent.repository.side;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;
import com.c203.altteulbe.room.persistent.entity.TeamRoom;
import com.c203.altteulbe.user.persistent.entity.User;

public interface SideProblemHistoryRepository extends JpaRepository<SideProblemHistory, Long> {
	List<SideProblemHistory> findByTeamRoomId(TeamRoom teamRoomId);

	List<SideProblemHistory> findByUserId(User userId);
	List<SideProblemHistory> findByUserIdAndGameIdAndResult(User user, Game game, SideProblemHistory.ProblemResult result);
}
