package com.c203.altteulbe.game.web.dto.judge.request.lang;

public class LangDtoFactory {
	public static String CPP_TEMPLATE;
	public static String JAVA_TEMPLATE = "//PREPEND BEGIN\nclass Main {\n//PREPEND END\n\n//TEMPLATE BEGIN\n  static int add(int a, int b) {\n    // code\n  }\n//TEMPLATE END\n\n//APPEND BEGIN\n  public static void main(String [] args) {\n    System.out.println(add(1, 2));\n  }\n}\n//APPEND END";
	public static String JS_TEMPLATE;
	public static String PYTHON3_TEMPLATE;
	public static CppLangDto createCppLangDto() {
		return CppLangDto.builder()
			.template(CPP_TEMPLATE)
			.compile(CppLangDto.CompileConfig.builder().build())
			.run(CppLangDto.RunConfig.builder().build())
			.build();
	}
	public static CppLangDto createCppLangDto(int maxCpuTime, int maxRealTime, int maxMemory) {
		return CppLangDto.builder()
			.template(CPP_TEMPLATE)
			.compile(CppLangDto.CompileConfig.builder()
				.max_cpu_time(maxCpuTime)
				.max_real_time(maxRealTime)
				.max_memory(maxMemory)
				.build()
			)
			.run(CppLangDto.RunConfig.builder().build())
			.build();
	}

	// Python3 팩토리 메서드
	public static Python3LangDto createPython3LangDto() {
		return Python3LangDto.builder()
			.template(PYTHON3_TEMPLATE)
			.compile(Python3LangDto.CompileConfig.builder().build())
			.run(Python3LangDto.RunConfig.builder().build())
			.build();
	}

	public static Python3LangDto createPython3LangDto(int maxCpuTime, int maxRealTime, int maxMemory) {
		return Python3LangDto.builder()
			.template(PYTHON3_TEMPLATE)
			.compile(Python3LangDto.CompileConfig.builder()
				.max_cpu_time(maxCpuTime)
				.max_real_time(maxRealTime)
				.max_memory(maxMemory)
				.build())
			.run(Python3LangDto.RunConfig.builder().build())
			.build();
	}

	// JavaScript 팩토리 메서드
	public static JSLangDto createJSLangDto() {
		return JSLangDto.builder()
			.template(JS_TEMPLATE)
			.compile(JSLangDto.CompileConfig.builder().build())
			.run(JSLangDto.RunConfig.builder().build())
			.build();
	}

	public static JSLangDto createJSLangDto(int maxCpuTime, int maxRealTime, int maxMemory) {
		return JSLangDto.builder()
			.template(JS_TEMPLATE)
			.compile(JSLangDto.CompileConfig.builder()
				.max_cpu_time(maxCpuTime)
				.max_real_time(maxRealTime)
				.max_memory(maxMemory)
				.build())
			.run(JSLangDto.RunConfig.builder().build())
			.build();
	}

	// Java 팩토리 메서드
	public static JavaLangDto createJavaLangDto() {
		return JavaLangDto.builder()
			.template(JAVA_TEMPLATE)
			.compile(JavaLangDto.CompileConfig.builder().build())
			.run(JavaLangDto.RunConfig.builder().build())
			.build();
	}

	public static JavaLangDto createJavaLangDto(int maxCpuTime, int maxRealTime, int maxMemory) {
		return JavaLangDto.builder()
			.template(JAVA_TEMPLATE)
			.compile(JavaLangDto.CompileConfig.builder()
				.max_cpu_time(maxCpuTime)
				.max_real_time(maxRealTime)
				.max_memory(maxMemory)
				.build())
			.run(JavaLangDto.RunConfig.builder().build())
			.build();
	}


}
