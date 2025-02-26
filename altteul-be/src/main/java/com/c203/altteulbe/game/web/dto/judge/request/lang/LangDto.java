package com.c203.altteulbe.game.web.dto.judge.request.lang;

import com.c203.altteulbe.common.dto.AbstractDto;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public class LangDto implements AbstractDto {
	private String template;
}