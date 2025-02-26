package com.c203.altteulbe.game.web.dto.judge.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PingResponse {
	private String err;
	private String judgerVersion;
	private String hostname;
	private Integer cpuCore;
	private Double cpu;
	private Double memory;
	private String action;
}
