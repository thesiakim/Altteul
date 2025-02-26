package com.c203.altteulbe.room.persistent.repository.single;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.room.persistent.entity.SingleRoom;

public interface SingleRoomRepository extends JpaRepository<SingleRoom, Long>, SingleRoomRepositoryCustom {
	Optional<SingleRoom> findByGameId(Long game_id);

	Optional<SingleRoom> findByUser_UserId(Long userId);

	Optional<SingleRoom> findByUser_UserIdAndActivationIsTrue(Long userId);

	Optional<SingleRoom> findByUser_UserIdAndGame(Long userId, Game game);
}
