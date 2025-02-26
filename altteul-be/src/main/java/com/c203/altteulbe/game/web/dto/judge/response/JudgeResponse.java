package com.c203.altteulbe.game.web.dto.judge.response;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class JudgeResponse {
	private String err;
	private Object data;

	public boolean isNotCompileError() {
		return err==null || !err.equals("CompileError");
	}

	public List<TestCaseResult> testDataGetter() {
		if (data instanceof List) {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.convertValue(data, mapper.getTypeFactory().constructCollectionType(List.class, TestCaseResult.class));
		}
		throw new BusinessException("받은 테스트 데이터가 잘못된 형식입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
