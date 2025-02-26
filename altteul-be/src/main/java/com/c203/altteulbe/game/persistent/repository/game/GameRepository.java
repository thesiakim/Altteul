package com.c203.altteulbe.game.persistent.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long>, GameCustomRepository {
}
