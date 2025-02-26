package com.c203.altteulbe.game.persistent.repository.side;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.game.persistent.entity.side.SideProblem;

public interface SideProblemRepository extends JpaRepository<SideProblem, Long>, SideProblemCustomRepository {
}
