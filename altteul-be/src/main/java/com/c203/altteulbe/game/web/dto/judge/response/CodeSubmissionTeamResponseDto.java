package com.c203.altteulbe.game.web.dto.judge.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmissionTeamResponseDto {
	private String status; // 채점 전체 결과 (예: "P": 합격, "F": 떨어짐)
	private Long userId;
	private List<TestCaseResponseDto> testCases;
	private Integer passCount;
	private Integer totalCount;

	public static CodeSubmissionTeamResponseDto from(JudgeResponse judgeResponse, Long id) {
		int passCount = 0;

		// 컴파일에러 아닌 경우 message : null, isNotCompileError : true, 테스트케이스 데이터 포함. 컴파일 에러인 경우 반대
		if (judgeResponse.isNotCompileError()) {
			List<TestCaseResponseDto> testCaseResponses = new ArrayList<>();
			List<TestCaseResult> testCaseResults = judgeResponse.testDataGetter();

			for (int i = 0; i < testCaseResults.size(); i++) {
				TestCaseResult testCaseResult = testCaseResults.get(i);
				boolean isPassed = testCaseResult.getResultEnum() == TestCaseResult.Result.P;
				if (isPassed)
					passCount++;

				testCaseResponses.add(TestCaseResponseDto.builder()
					.testCaseId((long)(i + 1))
					.testCaseNumber(i + 1)
					.status(isPassed ? "P" : "F")
					.executionTime(String.valueOf(testCaseResult.getCpu_time()))
					.executionMemory(String.valueOf(testCaseResult.getMemory()))
					.build());
			}

			return CodeSubmissionTeamResponseDto.builder()
				.userId(id)
				.passCount(passCount)
				.totalCount(testCaseResults.size())
				.testCases(testCaseResponses)
				.status(passCount == testCaseResults.size() ? "P" : "F")
				.build();
		} else {
			return CodeSubmissionTeamResponseDto.builder()
				.userId(id)
				.passCount(null)
				.totalCount(null)
				.testCases(null)
				.status("F")
				.build();
		}
	}
}
