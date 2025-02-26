package com.c203.altteulbe.user.web.dto.request;

import com.c203.altteulbe.common.dto.AbstractRequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto implements AbstractRequestDto {
	String username;
	String password;
}
