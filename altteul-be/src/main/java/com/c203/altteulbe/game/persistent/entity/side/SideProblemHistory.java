package com.c203.altteulbe.game.persistent.entity.side;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.room.persistent.entity.TeamRoom;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "side_problem_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class SideProblemHistory extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "side_problem_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game gameId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_room_id")
	private TeamRoom teamRoomId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "side_problem_id", nullable = false)
	private SideProblem sideProblemId;

	@Column(name = "user_answer", nullable = false, length = 128)
	private String userAnswer;

	@Column(name = "result", nullable = false)
	@Enumerated(EnumType.STRING)
	private ProblemResult result;

	public enum ProblemResult {
		P, F
	}
}