package com.c203.altteulbe.room.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TeamRoomInviteResponseDto {
	private Long roomId;
	private String nickname;

	public static TeamRoomInviteResponseDto create(Long roomId, String nickname) {
		return TeamRoomInviteResponseDto.builder()
										.roomId(roomId)
										.nickname(nickname)
										.build();
	}
}
