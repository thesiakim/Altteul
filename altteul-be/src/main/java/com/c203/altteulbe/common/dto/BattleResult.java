package com.c203.altteulbe.common.dto;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BattleResult {
	FAIL(0),

	FIRST(1),

	SECOND(2),

	THIRD(3),

	FOURTH(4),

	FIFTH(5),

	SIXTH(6),

	SEVENTH(7),

	EIGHTH(8);

	private final int rank;

	public static BattleResult fromRank(int rank) {
		return Arrays.stream(values())
			.filter(r -> r.rank == rank)
			.findFirst()
			.orElse(FAIL); // 기본값 FAIL
	}
}
