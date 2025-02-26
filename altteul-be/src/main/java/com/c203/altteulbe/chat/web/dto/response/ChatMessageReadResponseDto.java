package com.c203.altteulbe.chat.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageReadResponseDto {

	private Long chatroomId;

	private Long readerId;

	private Long senderId;

	private LocalDateTime readAt;

	private List<Long> messageIds;
}
