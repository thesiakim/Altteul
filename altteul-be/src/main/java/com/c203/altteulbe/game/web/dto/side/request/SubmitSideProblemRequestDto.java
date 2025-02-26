package com.c203.altteulbe.game.web.dto.side.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSideProblemRequestDto {
	Long gameId;
	Long teamId;
	Long sideProblemId;
	String answer;
}
