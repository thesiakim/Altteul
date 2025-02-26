package com.c203.altteulbe.chat.persistent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.chat.persistent.entity.UserChatRoom;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
	boolean existsByChatroom_ChatroomIdAndUser_UserId(Long chatRoomId, Long userId);
}
