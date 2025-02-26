package com.c203.altteulbe.common.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.c203.altteulbe.common.security.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	throws ServletException, IOException {
		try {
			//request에서 Authorization 헤더를 찾음
			String authorization = request.getHeader("Authorization");

			//Authorization 헤더 검증
			if (authorization == null || !authorization.startsWith("Bearer ")) {

				filterChain.doFilter(request, response);

				//조건이 해당되면 메소드 종료 (필수)
				return;
			}

			System.out.println("authorization now");
			//Bearer 부분 제거 후 순수 토큰만 획득
			String token = authorization.split(" ")[1];

			//토큰 소멸 시간 검증
			if (jwtUtil.isExpired(token)) {
				System.out.println("token expired");
				filterChain.doFilter(request, response);

				//조건이 해당되면 메소드 종료 (필수)
				return;
			}

			Long id = jwtUtil.getId(token);

			//스프링 시큐리티 인증 토큰 생성
			Authentication authToken = new UsernamePasswordAuthenticationToken(id, null, List.of(new SimpleGrantedAuthority("USER")));

			//세션에 사용자 등록
			SecurityContextHolder.getContext().setAuthentication(authToken);

		} catch (Exception e) {
			request.setAttribute("exception", e);
		}

		filterChain.doFilter(request, response);
	}
}