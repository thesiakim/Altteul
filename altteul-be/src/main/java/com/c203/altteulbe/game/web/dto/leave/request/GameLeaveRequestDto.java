package com.c203.altteulbe.game.web.dto.leave.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameLeaveRequestDto {
	private Long roomId;
}
