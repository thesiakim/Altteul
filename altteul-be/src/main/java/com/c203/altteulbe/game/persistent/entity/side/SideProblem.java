package com.c203.altteulbe.game.persistent.entity.side;

import java.time.LocalDateTime;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "side_problem")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class SideProblem extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "side_problem_id")
	private Long id;

	@Column(name = "side_problem_title", nullable = false, length = 128)
	private String title;

	@Column(name = "side_problem_content", nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "side_problem_answer", nullable = false, length = 255)
	private String answer;

}