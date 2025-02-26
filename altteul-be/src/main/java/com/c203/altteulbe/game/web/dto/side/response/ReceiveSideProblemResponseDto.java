package com.c203.altteulbe.game.web.dto.side.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveSideProblemResponseDto {
	Long id;
	String title;
	String description;
}
