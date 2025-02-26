package com.c203.altteulbe.game.web.dto.judge.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResult {
	private int cpu_time;
	private int real_time;
	private int memory;
	private int signal;
	private int exit_code;
	private int error;
	private int result;
	private String test_case;
	private String output_md5;
	private String output;

	// 결과 해석을 위한 enum
	public enum Result {
		F(-1),
		P(0),
		CLE(1),
		TLE(2),
		MLE(3),
		RUN(4),
		SYS(5);

		private final int value;

		Result(int value) {
			this.value = value;
		}

		public static Result fromValue(int value) {
			for (Result result : values()) {
				if (result.value == value) {
					return result;
				}
			}
			throw new IllegalArgumentException("Unknown result value: " + value);
		}
	}

	// 에러 해석을 위한 enum
	public enum Error {
		P(0),
		INVALID_CONFIG(-1),
		CLONE_FAILED(-2),
		PTHREAD_FAILED(-3),
		WAIT_FAILED(-4),
		ROOT_REQUIRED(-5),
		LOAD_SECCOMP_FAILED(-6),
		SETRLIMIT_FAILED(-7),
		DUP2_FAILED(-8),
		SETUID_FAILED(-9),
		EXECVE_FAILED(-10),
		SPJ_ERROR(-11);

		private final int value;

		Error(int value) {
			this.value = value;
		}

		public static Error fromValue(int value) {
			for (Error error : values()) {
				if (error.value == value) {
					return error;
				}
			}
			throw new IllegalArgumentException("Unknown error value: " + value);
		}
	}

	// 결과 해석 메서드
	public Result getResultEnum() {
		return Result.fromValue(this.result);
	}

	// 에러 해석 메서드
	public Error getErrorEnum() {
		return Error.fromValue(this.error);
	}
}
