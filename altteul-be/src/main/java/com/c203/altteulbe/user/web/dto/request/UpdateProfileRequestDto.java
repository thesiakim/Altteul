package com.c203.altteulbe.user.web.dto.request;

import com.c203.altteulbe.common.dto.Language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDto {
	String nickname;
	Language mainLang;
}
