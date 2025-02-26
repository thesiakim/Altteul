package com.c203.altteulbe.game.web.dto.item.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseItemResponseDto {
	Long itemId;
	String itemName;
}
