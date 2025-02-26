package com.c203.altteulbe.common.security.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.c203.altteulbe.common.security.utils.JWTUtil;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.web.dto.request.LoginRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/login");  // 로그인 경로를 /api/login으로 설정
	}
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LoginRequestDto loginRequestDto = null;
		try {
			// ServletRequest의 InputStream을 String으로 변환
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = request.getReader();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			System.out.println("Request Body: " + stringBuilder.toString());
			// JSON 문자열을 LoginRequest 객체로 변환
			ObjectMapper objectMapper = new ObjectMapper();
			loginRequestDto = objectMapper.readValue(stringBuilder.toString(), LoginRequestDto.class);
			} catch (JsonMappingException e) {
				throw new RuntimeException(e);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			String username = loginRequestDto.getUsername();
			String password = loginRequestDto.getPassword();

			// 로깅 추가 (디버깅용)
			logger.debug("Attempting authentication for ID: " + username);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
				List.of(new SimpleGrantedAuthority("USER")));
			return authenticationManager.authenticate(authToken);
	}

	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		User userDetails = (User) authentication.getPrincipal();

		String token = jwtUtil.createJwt(userDetails.getUserId(), 60*60*10000L);
		log.info("token={}", token);

		response.addHeader("Authorization", "Bearer " + token);
		response.addHeader("userid", userDetails.getUserId().toString());
	}
}