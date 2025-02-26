package com.c203.altteulbe.user.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateIdRequestDto {
	@NotNull(message = "Username is required")
	private String username;
}
