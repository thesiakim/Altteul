package com.c203.altteulbe.user.web.dto.request;

import com.c203.altteulbe.common.dto.AbstractRequestDto;
import com.c203.altteulbe.common.dto.Language;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequestDto implements AbstractRequestDto {

	@NotNull(message = "Username is required")
	private String username;

	@NotNull(message = "Password is required")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	private String password;

	@NotNull(message = "Nickname is required")
	private String nickname;

	@NotNull(message = "Main language is required")
	private Language mainLang;
}