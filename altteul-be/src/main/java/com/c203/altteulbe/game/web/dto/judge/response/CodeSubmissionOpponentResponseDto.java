package com.c203.altteulbe.game.web.dto.judge.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeSubmissionOpponentResponseDto {
	private Long userId;
	private String status;
	private Integer passCount;
	private Integer totalCount;
}

