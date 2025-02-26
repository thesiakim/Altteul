package com.c203.altteulbe.room.web.dto.response;

import com.c203.altteulbe.aws.util.S3Util;
import com.c203.altteulbe.user.persistent.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SingleRoomGameStartForUserInfoResponseDto {
	private Long roomId;
	private Long userId;
	private String nickname;
	private String profileImg;
	private Long tierId;

	public static SingleRoomGameStartForUserInfoResponseDto fromEntity(User user, Long roomId) {
		return SingleRoomGameStartForUserInfoResponseDto.builder()
					.roomId(roomId)
					.userId(user.getUserId())
					.nickname(user.getNickname())
					.profileImg(S3Util.getImgUrl(user.getProfileImg()))
					.tierId(user.getTier().getId())
					.build();
	}
}
