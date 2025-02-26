package com.c203.altteulbe.friend.persistent.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendId implements Serializable {
	// 유저 ID와 친구 ID를 합쳐서 복합키로 만듬
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "friend_id", nullable = false)
	private Long friendId;
}
