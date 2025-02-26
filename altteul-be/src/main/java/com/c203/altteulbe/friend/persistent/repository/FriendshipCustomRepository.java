package com.c203.altteulbe.friend.persistent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.c203.altteulbe.friend.persistent.entity.Friendship;

public interface FriendshipCustomRepository {
	Page<Friendship> findAllByUserIdWithFriend(long userId, Pageable pageable);

	boolean existsByUserAndFriend(Long userId, Long friendId);
}
