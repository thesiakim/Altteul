package com.c203.altteulbe.game.web.dto.result.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
public class AIFeedbackRequestDto {
	Long gameId;
	Long teamId;
}
