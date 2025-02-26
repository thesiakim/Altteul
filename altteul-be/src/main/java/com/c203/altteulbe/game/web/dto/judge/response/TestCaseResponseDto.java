package com.c203.altteulbe.game.web.dto.judge.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponseDto {
	private Long testCaseId;
	private int testCaseNumber;
	private String status;
	private String executionTime;
	private String executionMemory;
}