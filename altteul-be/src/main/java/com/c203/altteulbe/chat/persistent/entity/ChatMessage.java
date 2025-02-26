package com.c203.altteulbe.chat.persistent.entity;

import com.c203.altteulbe.common.entity.BaseCreatedAndUpdatedEntity;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ChatMessage extends BaseCreatedAndUpdatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatMessageId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Chatroom chatroom;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;

	private String messageContent;

	@Column(nullable = false)
	private boolean checked;

}
