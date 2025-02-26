package com.c203.altteulbe.friend.persistent.entity;

import org.springframework.data.domain.Persistable;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Persistable은 Spring Data JPA가 엔티티가 새로운 것인지(INSERT) 기존 데이터인지(UPDATE) 판단할 때 사용
 * 기본적으로 JPA는 ID 값이 null이면 새로운 엔티티(INSERT)로 인식하지만,
 * 복합키(EmbeddedId)를 사용하면 ID가 null인지로 판별하기 어려움 → 직접 isNew()를 구현해야 함
 */

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Friendship extends BaseCreatedEntity implements Persistable<FriendId> {
	@EmbeddedId
	private FriendId id; // 복합키

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@MapsId("userId")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "friend_id")
	@MapsId("friendId")
	private User friend;

	@Override
	public FriendId getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return super.getCreatedAt() == null;
	}

	// 양방향 관계 생성을 위한 메소드
	public static Friendship createFriendship(User user, User friend) {
		return Friendship.builder()
			.id(new FriendId(user.getUserId(), friend.getUserId()))
			.user(user)
			.friend(friend)
			.build();
	}
}
