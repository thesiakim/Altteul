package com.c203.altteulbe.chat.persistent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.chat.persistent.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageCustomRepository  {
}
