package com.c203.altteulbe.chat.web.dto.request;

import com.c203.altteulbe.chat.persistent.entity.ChatMessage;
import com.c203.altteulbe.chat.persistent.entity.Chatroom;
import com.c203.altteulbe.user.persistent.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequestDto {

	private Long chatroomId;

	private Long senderId;

	private String content;

	// Dto -> Entity
	public static ChatMessage to(ChatMessageRequestDto dto, Chatroom chatroom, User sender) {
		return ChatMessage.builder()
			.chatroom(chatroom)
			.sender(sender)
			.messageContent(dto.getContent())
			.checked(false)
			.build();
	}
}
