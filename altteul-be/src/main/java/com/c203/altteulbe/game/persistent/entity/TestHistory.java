package com.c203.altteulbe.game.persistent.entity;

import java.util.ArrayList;
import java.util.List;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.persistent.entity.problem.Problem;
import com.c203.altteulbe.game.web.dto.judge.response.CodeSubmissionTeamResponseDto;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "test_history")
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TestHistory extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	@Column(name = "code", nullable = false, columnDefinition = "LONGTEXT")
	private String code;

	@Column(name = "execute_time", columnDefinition = "VARCHAR(20)")
	private String executeTime;

	@Column(name = "execute_memory", columnDefinition = "VARCHAR(20)")
	private String executeMemory;

	@Column(name = "result", nullable = false, columnDefinition = "ENUM('P','F','C')")
	@Enumerated(EnumType.STRING)
	private Status result;

	@Column(name = "success_count", columnDefinition = "TINYINT")
	private Integer successCount;

	@Column(name = "fail_count", columnDefinition = "TINYINT")
	private Integer failCount;

	@OneToMany(mappedBy = "testHistory", cascade = CascadeType.ALL)
	@Builder.Default
	private List<TestResult> testResults = new ArrayList<>();

	@Getter
	public enum Status {
		P,
		F,
		C
	}

	public static TestHistory from(CodeSubmissionTeamResponseDto teamResponseDto, Long gameId, Long problemId, Long userId, String maxExecuteTime, String maxMemory,  String code, String lang) {
		Integer failCount;
		Integer successCount;
		if (teamResponseDto.getTotalCount() == null) {
			failCount = null;
			successCount = null;
		} else {
			successCount = teamResponseDto.getPassCount();
			failCount = teamResponseDto.getTotalCount() - successCount;
		}
		return TestHistory.builder()
			.game(Game.builder().id(gameId).build())
			.user(User.builder().userId(userId).build())
			.problem(Problem.builder().id(problemId).build())
			.code(code)
			.executeTime(maxExecuteTime)
			.executeMemory(maxMemory)
			.successCount(successCount)
			.failCount(failCount)
			.result(Status.valueOf(teamResponseDto.getStatus()))
			.build();
	}

	public void updateTestResults(List<TestResult> testResults) {
		this.testResults.clear();
		this.testResults.addAll(testResults);
	}
}