package com.c203.altteulbe.game.web.dto.judge.request.lang;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class Python3LangDto extends LangDto {
	private CompileConfig compile;
	private RunConfig run;

	@SuperBuilder
	@Getter
	public static class CompileConfig {
		private final String src_name = "solution.py";
		private final String exe_name = "solution.py";
		private final String compile_command = "/usr/bin/python3 -m py_compile {src_path}";
		@Builder.Default
		private int max_cpu_time = 3000;
		@Builder.Default
		private int max_real_time = 10000;
		@Builder.Default
		private int max_memory = 128 * 1024 * 1024;
	}

	@SuperBuilder
	@Getter
	public static class RunConfig extends CommonRunConfig {
		private final String command = "/usr/bin/python3 -BS {exe_path}";
		private final String seccomp_rule = "general";
	}
}