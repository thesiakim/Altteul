package com.c203.altteulbe.chat.persistent.repository;

import java.util.List;

import com.c203.altteulbe.chat.persistent.entity.ChatMessage;

public interface ChatMessageCustomRepository {
	void updateMessageAsRead(List<Long> messageIds);

	List<ChatMessage> findUnreadMessages(Long chatroomId, Long readerId);
}
