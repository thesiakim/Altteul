package com.c203.altteulbe.common.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.c203.altteulbe.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// public class PromptTemplateLoader {
//
// 	@Value("classpath:prompts/system-prompt.st")
// 	private Resource systemPromptResource;
//
// 	public String loadSystemPrompt() {
// 		Resource resource = systemPromptResource;
//
// 		try {
// 			return new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);
// 		} catch (IOException e) {
// 			log.error("Error loading system prompt template", e);
// 			throw new BusinessException("시스템 프롬프트 오류", HttpStatus.INTERNAL_SERVER_ERROR);
// 		}
// 	}
// 	//
// 	//    public String loadUserPrompt() {
// 	//        try {
// 	//            return new String(FileCopyUtils.copyToByteArray(userPromptResource.getInputStream()), StandardCharsets.UTF_8);
// 	//        } catch (IOException e) {
// 	//            log.error("Error loading user prompt template", e);
// 	//            throw new RuntimeException("Failed to load user prompt template", e);
// 	//        }
// 	//    }
// }