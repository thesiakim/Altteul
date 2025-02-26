package com.c203.altteulbe.friend.persistent.entity;

import com.c203.altteulbe.common.dto.RequestStatus;
import com.c203.altteulbe.common.entity.BaseCreatedAndUpdatedEntity;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class FriendRequest extends BaseCreatedAndUpdatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long friendRequestId;

	@ManyToOne(fetch = FetchType.LAZY)
	private User from;

	@ManyToOne(fetch = FetchType.LAZY)
	private User to;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 1)
	private RequestStatus requestStatus;

	@Builder
	public FriendRequest(User from, User to) {
		this.from = from;
		this.to = to;
		this.requestStatus = RequestStatus.P;
	}

	public void updateStatus(RequestStatus status) {
		this.requestStatus = status;
	}
}
