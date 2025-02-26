package com.c203.altteulbe.room.persistent.repository.team;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.room.persistent.entity.TeamRoom;

public interface TeamRoomRepository extends JpaRepository<TeamRoom, Long> {
	Optional<TeamRoom> findByGameId(Long game_id);
}
