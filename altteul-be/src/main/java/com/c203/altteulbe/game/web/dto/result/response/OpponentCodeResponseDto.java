package com.c203.altteulbe.game.web.dto.result.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpponentCodeResponseDto {
	private List<UserCodeInGameResponseDto> userCodes;
}
