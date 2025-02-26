package com.c203.altteulbe.game.web.dto.result.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AIFeedbackResponse {
	String content;
}
