package com.c203.altteulbe.game.web.dto.judge.request.lang;

import java.util.List;

import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
public class CommonRunConfig {
	private final List<String> env = List.of("LANG=en_US.UTF-8", "LANGUAGE=en_US:en", "LC_ALL=en_US.UTF-8");
}
