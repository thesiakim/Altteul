package com.c203.altteulbe.game.web.dto.judge.request.lang;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class CppLangDto extends LangDto {
	private CompileConfig compile;
	private RunConfig run;

	@SuperBuilder
	@Getter
	public static class CompileConfig {
		private final String src_name = "main.cpp";
		private final String exe_name = "main";
		private final String compile_command = "/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++20 {src_path} -lm -o {exe_path}";
		@Builder.Default
		private int max_cpu_time = 10000;
		@Builder.Default
		private int max_real_time = 20000;
		@Builder.Default
		private int max_memory = 1024 * 1024 * 1024;
	}

	@SuperBuilder
	@Getter
	public static class RunConfig extends CommonRunConfig {
		private final String command = "{exe_path}";
		private final String seccomp_rule = null;
	}
}
