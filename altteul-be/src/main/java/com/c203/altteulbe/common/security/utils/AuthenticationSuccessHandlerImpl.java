package com.c203.altteulbe.common.security.utils;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

	private final JWTUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		User userDetails = (User) authentication.getPrincipal();
		String token = jwtUtil.createJwt(userDetails.getUserId(), 60*60*10000L);

		String targetUrl = UriComponentsBuilder.fromUriString("https://i12c203.p.ssafy.io:443/")
			.queryParam("accessToken", token)
			.queryParam("userId", userDetails.getUserId())
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

}