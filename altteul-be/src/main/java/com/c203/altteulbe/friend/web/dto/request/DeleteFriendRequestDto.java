package com.c203.altteulbe.friend.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteFriendRequestDto {

	private Long userId;

	private Long friendId;
}
