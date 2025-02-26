package com.c203.altteulbe.common.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResponseBody {
	@Getter
	@AllArgsConstructor
	public static class Failure implements Serializable {

		private int status;
		private String message;
	}

	@Getter
	@AllArgsConstructor
	public static class Success<D> implements Serializable {
		private D data;
		private String message;
		private int status;
	}
}
