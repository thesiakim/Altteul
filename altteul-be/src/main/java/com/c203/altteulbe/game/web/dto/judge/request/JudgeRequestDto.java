package com.c203.altteulbe.game.web.dto.judge.request;

import com.c203.altteulbe.game.web.dto.judge.request.lang.LangDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JudgeRequestDto {
	String src;
	LangDto language_config;
	Long max_cpu_time;
	Long max_memory;
	String test_case_id;
	Boolean output;
}