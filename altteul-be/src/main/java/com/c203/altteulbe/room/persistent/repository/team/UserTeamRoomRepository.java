package com.c203.altteulbe.room.persistent.repository.team;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.room.persistent.entity.UserTeamRoom;
import com.c203.altteulbe.room.persistent.entity.UserTeamRoomId;

public interface UserTeamRoomRepository extends JpaRepository<UserTeamRoom, UserTeamRoomId> {
	Optional<UserTeamRoom> findByUser_UserId(Long userId);

	Optional<UserTeamRoom> findByUser_UserIdAndTeamRoom_Game(Long userId, Game game);
}
