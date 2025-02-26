package com.c203.altteulbe.friend.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendStatusUpdateResponseDto {
	private Long userId;
	private String status;
}
