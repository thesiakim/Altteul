package com.c203.altteulbe.game.persistent.entity;

import java.util.ArrayList;
import java.util.List;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.web.dto.judge.response.JudgeResponse;
import com.c203.altteulbe.game.web.dto.judge.response.TestCaseResult;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "test_result")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TestResult extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_result_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_history_id", nullable = false)
	private TestHistory testHistory;

	@Column(name = "execute_time", columnDefinition = "int unsigned")
	private Integer executeTime;

	@Column(name = "execute_memory", columnDefinition = "int unsigned")
	private Integer executeMemory;

	@Column(name = "test_result", nullable = false, columnDefinition = "ENUM('P','RUN','TLE','MLE','F')")
	@Enumerated(EnumType.STRING)
	private Status testResult;

	@Column(name = "user_output", columnDefinition = "TEXT")
	private String userOutput;

	@Getter
	public enum Status {
		P,
		F,
		RUN,
		TLE,
		MLE
	}

	// from 메서드
	public static List<TestResult> from(JudgeResponse judgeResponse, TestHistory testHistory) {
		List<TestResult> testResults = new ArrayList<>();

		if (judgeResponse.isNotCompileError()) {
			List<TestCaseResult> testCaseResults = judgeResponse.testDataGetter();
			for (TestCaseResult testCaseResult : testCaseResults) {
				Status status;
				switch (testCaseResult.getResultEnum()) {
					case F -> status = Status.F;
					case P -> status = Status.P;
					case TLE, CLE -> status = Status.TLE;
					case MLE -> status = Status.MLE;
					default -> status = Status.RUN;
				}
				System.out.println("status: " + status);
				System.out.println("output: " + testCaseResult.getOutput());
				testResults.add(
					TestResult.builder()
						.testResult(status)
						.userOutput(testCaseResult.getOutput())
						.executeMemory(testCaseResult.getMemory())
						.executeTime(testCaseResult.getCpu_time())
						.testHistory(testHistory)
						.build()
				);
			}
			return testResults;
		} else {
			return null;
		}
	}
}