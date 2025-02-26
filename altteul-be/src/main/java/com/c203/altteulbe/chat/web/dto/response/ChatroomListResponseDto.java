package com.c203.altteulbe.chat.web.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomListResponseDto {

	private Long friendId;

	private String nickname;

	private String profileImg;

	private Boolean isOnline;

	private String recentMessage;

	private Boolean isMessageRead;

	private LocalDateTime createdAt;
}
