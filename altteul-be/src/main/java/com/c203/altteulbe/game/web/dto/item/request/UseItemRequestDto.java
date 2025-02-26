package com.c203.altteulbe.game.web.dto.item.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseItemRequestDto {
	private Long gameId;
	private Long teamId;
	private Long itemId;
}
