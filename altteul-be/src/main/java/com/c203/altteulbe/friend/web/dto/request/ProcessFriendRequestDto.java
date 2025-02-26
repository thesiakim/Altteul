package com.c203.altteulbe.friend.web.dto.request;

import com.c203.altteulbe.common.dto.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessFriendRequestDto {
	private Long friendRequestId;

	private Long fromUserId;

	private Long toUserId;

	private RequestStatus requestStatus;
}
