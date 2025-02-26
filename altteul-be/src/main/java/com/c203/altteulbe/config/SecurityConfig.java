package com.c203.altteulbe.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.c203.altteulbe.common.security.filter.JWTFilter;
import com.c203.altteulbe.common.security.filter.LoginFilter;
import com.c203.altteulbe.common.security.utils.JWTUtil;
import com.c203.altteulbe.common.security.utils.JwtAccessDeniedHandler;
import com.c203.altteulbe.common.security.utils.JwtAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // 스프링 시큐리티 어노테이션 활성화를 위해서
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final DefaultOAuth2UserService defaultOAuth2UserService;
	private final JwtAuthenticationEntryPoint entryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable); //csrf 안써요
		http
			.formLogin(AbstractHttpConfigurer::disable); //폼 로그인 방식 안써요
		http.httpBasic(AbstractHttpConfigurer::disable); //모름

		http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration configuration = new CorsConfiguration();
				configuration.setAllowedOriginPatterns(Arrays.asList(
					"http://localhost:7080",
					"http://localhost:443",
					"http://localhost:5173",
					"http://frontend:7080",
					"http://frontend:443",
					"http://frontend:5173",
					"http://host.docker.internal:7080",
					"http://host.docker.internal:443",
					"http://host.docker.internal:5173",
					"https://localhost:7080",
					"https://localhost:443",
					"https://localhost:5173",
					"https://frontend:7080",
					"https://frontend:443",
					"https://frontend:5173",
					"https://host.docker.internal:7080",
					"https://host.docker.internal:443",
					"https://host.docker.internal:5173",
					"https://i12c203.p.ssafy.io",
					"https://i12c203.p.ssafy.io:443",
					"https://i12c203.p.ssafy.io:8443",
					"https://i12c203.p.ssafy.io:7880",  // Internal HTTP
					"https://i12c203.p.ssafy.io:9000",  // MinIO
					"https://i12c203.p.ssafy.io:7881"   // WebRTC TCP
				));

				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);
				configuration.setExposedHeaders(List.of("Authorization", "userid"));

				return configuration;
			}
		})));

		http.authorizeHttpRequests((auth) -> auth
			.requestMatchers(HttpMethod.GET).permitAll()
			.requestMatchers(HttpMethod.POST).permitAll()
			.requestMatchers("/ws/**").permitAll()
			.requestMatchers("/api/admin").authenticated()
			.requestMatchers("/api/login", "/api/register", "/api/judge-ping-check").permitAll()
			.requestMatchers(HttpMethod.POST).authenticated()
			.requestMatchers(HttpMethod.PUT).authenticated()
			.requestMatchers(HttpMethod.DELETE).authenticated()
			.anyRequest().permitAll());
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
			.exceptionHandling(handler
				-> handler.authenticationEntryPoint(entryPoint).accessDeniedHandler(jwtAccessDeniedHandler));
		//loginfilter 쓸거임
		http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
			UsernamePasswordAuthenticationFilter.class);

		//인가 stateless
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http
			.oauth2Login(oauth -> oauth
				.successHandler(authenticationSuccessHandler)
				.userInfoEndpoint(user -> user
					.userService(defaultOAuth2UserService)
				)
			);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}