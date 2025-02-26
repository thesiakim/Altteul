package com.c203.altteulbe.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BattleType {
	S(2, 8),     // Single Battle : 최소 2명, 최대 8명
	T(2, 4);     // Team Battle : 최소 2명, 최대 4명

	private final int minUsers;
	private final int maxUsers;
}
