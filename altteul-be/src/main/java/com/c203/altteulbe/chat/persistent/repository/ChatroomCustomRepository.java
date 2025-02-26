package com.c203.altteulbe.chat.persistent.repository;

import java.util.List;
import java.util.Optional;

import com.c203.altteulbe.chat.web.dto.response.ChatroomDetailResponseDto;
import com.c203.altteulbe.chat.web.dto.response.ChatroomListResponseDto;

public interface ChatroomCustomRepository {
	List<ChatroomListResponseDto> findAllChatroomsByUserId(Long userId);

	Optional<ChatroomDetailResponseDto> findChatroomById(Long chatroomId, Long userId);

	Optional<ChatroomDetailResponseDto> findExistingChatroom(Long userId, Long friendId);

}
