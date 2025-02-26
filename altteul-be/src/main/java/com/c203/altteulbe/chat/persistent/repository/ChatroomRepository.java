package com.c203.altteulbe.chat.persistent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.chat.persistent.entity.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long>, ChatroomCustomRepository {
}
