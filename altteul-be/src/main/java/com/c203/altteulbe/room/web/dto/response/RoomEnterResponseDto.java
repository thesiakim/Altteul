package com.c203.altteulbe.room.web.dto.response;

import java.util.List;

import com.c203.altteulbe.user.web.dto.response.UserInfoResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class RoomEnterResponseDto {
	private Long roomId;
	private Long leaderId;
	private List<UserInfoResponseDto> users;

	public static RoomEnterResponseDto from(Long roomId, Long leaderId, List<UserInfoResponseDto> users) {
		return RoomEnterResponseDto.builder()
										 .roomId(roomId)
									     .leaderId(leaderId)
									     .users(users)
										 .build();
	}
}
