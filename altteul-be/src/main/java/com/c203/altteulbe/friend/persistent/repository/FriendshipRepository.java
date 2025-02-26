package com.c203.altteulbe.friend.persistent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.friend.persistent.entity.FriendId;
import com.c203.altteulbe.friend.persistent.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendId>, FriendshipCustomRepository {

}
