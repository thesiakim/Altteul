package com.c203.altteulbe.user.web.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.c203.altteulbe.aws.util.S3Util;
import com.c203.altteulbe.user.persistent.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDto {
	private Long userId;
	private String nickname;
	private String profileImg;
	private Long tierId;

	// User Entity → UserInfoResponseDto
	public static UserInfoResponseDto fromEntity(User user) {
		return UserInfoResponseDto.builder()
								  .userId(user.getUserId())
								  .nickname(user.getNickname())
								  .profileImg(S3Util.getImgUrl(user.getProfileImg()))
								  .tierId(user.getTier().getId())
								  .build();
	}

	// User Entity List → UserInfoResponseDto List
	public static List<UserInfoResponseDto> fromEntities(List<User> users) {
		return users.stream()
					.map(UserInfoResponseDto::fromEntity)
					.collect(Collectors.toList());
	}
}
