package com.c203.altteulbe.game.web.dto.judge.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class SubmitCodeRequestDto {
	private Long gameId;
	private Long teamId;
	private Long problemId;
	private String lang; // ENUM: "PY", "JV", "CPP", "JS" 등
	private String code;
}
