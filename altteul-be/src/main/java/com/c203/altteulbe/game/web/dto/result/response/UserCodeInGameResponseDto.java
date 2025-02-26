package com.c203.altteulbe.game.web.dto.result.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCodeInGameResponseDto {
	private String code;
	private String nickname;
}
