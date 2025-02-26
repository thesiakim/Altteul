package com.c203.altteulbe.game.web.dto.judge.request.lang;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class JSLangDto extends LangDto {
	private CompileConfig compile;
	private RunConfig run;

	@SuperBuilder
	@Getter
	public static class CompileConfig extends CommonRunConfig {
		private final String src_name = "main.js";
		private final String exe_name = "main.js";
		private final String compile_command = "/usr/bin/node --check {src_path}";
		@Builder.Default
		private int max_cpu_time = 30000;
		@Builder.Default
		private int max_real_time = 50000;
		@Builder.Default
		private int max_memory = 1024 * 1024 * 1024;
	}

	@SuperBuilder
	@Getter
	public static class RunConfig extends CommonRunConfig {
		private final String command = "/usr/bin/node {exe_path}";
		private final String seccomp_rule = "node";
		private final int memory_limit_check_only = 1;
	}
}
