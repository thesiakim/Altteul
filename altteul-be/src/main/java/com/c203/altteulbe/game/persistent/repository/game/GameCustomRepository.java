package com.c203.altteulbe.game.persistent.repository.game;

import java.util.List;
import java.util.Optional;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.game.persistent.entity.Game;

public interface GameCustomRepository {
	List<Game>  findWithItemAndProblemAndAllMemberByUserId(Long userId);

	Optional<Game> findWithAllMemberByGameId(Long gameId);

	Optional<Game> findWithRoomByGameId(Long gameId);

	Optional<Game> findWithRoomAndProblemByGameIdAndTeamId(Long gameId, Long teamId);

	Optional<Game> findWithGameByRoomIdAndType(Long roomId, BattleType battleType);
}
