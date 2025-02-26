package com.c203.altteulbe.config;

import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JudgeServerConfig {

	@Value("${judge.server.key}")
	private String judgeServerToken;

	@Value("${spring.jwt.secret}")
	private String secretKey;

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			// 헤더에 X-Judge-Server-Token 설정
			String hashedToken = HashUtil.sha256(secretKey);
			System.out.println("hashedToken: " + hashedToken);
			request.getHeaders().add("X-Judge-Server-Token", hashedToken);
			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			return execution.execute(request, body);
		});
		return restTemplate;
	}

	public static class HashUtil {
		public static String sha256(String input) {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(input.getBytes("UTF-8"));

				// Convert byte array into hexadecimal string
				StringBuilder hexString = new StringBuilder();
				for (byte b : hash) {
					String hex = Integer.toHexString(0xff & b);
					if (hex.length() == 1) hexString.append('0');
					hexString.append(hex);
				}
				return hexString.toString();
			} catch (Exception e) {
				throw new RuntimeException("Error hashing string: " + e.getMessage(), e);
			}
		}
	}
}

