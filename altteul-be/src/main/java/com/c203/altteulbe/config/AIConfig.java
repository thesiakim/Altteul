package com.c203.altteulbe.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

	@Value("${open-api-key}")
	String openApiKey;

	@Bean
	public ChatModel openAIChatModel(OpenAiApi openAIApi) {
		return new OpenAiChatModel(openAIApi);
	}

	@Bean

	public OpenAiApi openAIApi() {
		return new OpenAiApi(openApiKey); // 환경변수에서 API 키를 가져옴
	}
}