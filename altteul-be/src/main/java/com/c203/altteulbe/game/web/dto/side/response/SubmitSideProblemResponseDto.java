package com.c203.altteulbe.game.web.dto.side.response;

import com.c203.altteulbe.game.persistent.entity.side.SideProblemHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitSideProblemResponseDto {
	Long roomId;
	SideProblemHistory.ProblemResult status;
	Long itemId;
	String itemName;
	int bonusPoint;
}
