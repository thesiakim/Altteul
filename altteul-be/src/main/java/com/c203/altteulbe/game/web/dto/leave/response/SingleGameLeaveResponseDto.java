package com.c203.altteulbe.game.web.dto.leave.response;

import java.util.List;

import com.c203.altteulbe.user.web.dto.response.UserInfoResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleGameLeaveResponseDto {
	private Long gameId;
	private Long roomId;
	private UserInfoResponseDto leftUser;
	private List<UserInfoResponseDto> remainingUsers;

	public static SingleGameLeaveResponseDto of(Long gameId, Long roomId,
		UserInfoResponseDto leftUser,
		List<UserInfoResponseDto> remainingUsers) {
		return SingleGameLeaveResponseDto.builder()
			.gameId(gameId)
			.roomId(roomId)
			.leftUser(leftUser)
			.remainingUsers(remainingUsers)
			.build();
	}
}
