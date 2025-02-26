package com.c203.altteulbe.chat.persistent.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.aws.util.S3Util;
import com.c203.altteulbe.chat.persistent.entity.ChatMessage;
import com.c203.altteulbe.chat.persistent.entity.Chatroom;
import com.c203.altteulbe.chat.persistent.entity.QChatMessage;
import com.c203.altteulbe.chat.persistent.entity.QChatroom;
import com.c203.altteulbe.chat.persistent.entity.QUserChatRoom;
import com.c203.altteulbe.chat.web.dto.response.ChatMessageResponseDto;
import com.c203.altteulbe.chat.web.dto.response.ChatroomDetailResponseDto;
import com.c203.altteulbe.chat.web.dto.response.ChatroomListResponseDto;
import com.c203.altteulbe.friend.persistent.entity.QFriendship;
import com.c203.altteulbe.friend.service.UserStatusService;
import com.c203.altteulbe.user.persistent.entity.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ChatroomCustomRepositoryImpl extends QuerydslRepositorySupport implements ChatroomCustomRepository {
	private static final Logger log = LoggerFactory.getLogger(ChatroomCustomRepositoryImpl.class);
	private final JPAQueryFactory queryFactory;
	private final UserStatusService userStatusService;

	private static final QChatMessage Q_CHATMESSAGE = QChatMessage.chatMessage;
	private static final QChatroom Q_CHATROOM = QChatroom.chatroom;
	private static final QUserChatRoom Q_USER_CHATROOM = QUserChatRoom.userChatRoom;
	private static final QUser Q_USER = QUser.user;
	private static final QFriendship Q_FRIENDSHIP = QFriendship.friendship;

	public ChatroomCustomRepositoryImpl(JPAQueryFactory queryFactory, UserStatusService userStatusService) {
		super(Chatroom.class);
		this.queryFactory = queryFactory;
		this.userStatusService = userStatusService;
	}

	// 유저의 모든 채팅방 조회
	@Override
	public List<ChatroomListResponseDto> findAllChatroomsByUserId(Long userId) {

		List<ChatroomListResponseDto> chatrooms = queryFactory
			.select(Projections.constructor(ChatroomListResponseDto.class,
				Q_USER.userId,
				Q_USER.nickname,
				Q_USER.profileImg,
				Expressions.constant(Boolean.FALSE), // 유저의 온라인 상태
				Q_CHATMESSAGE.messageContent,
				Expressions.as(
					Expressions.cases()          // 내가 읽었는지 확인하는 로직
						.when(
							JPAExpressions
								.selectOne()
								.from(Q_CHATMESSAGE)
								.where(Q_CHATMESSAGE.chatroom.eq(Q_CHATROOM)
									.and(Q_CHATMESSAGE.sender.userId.ne(userId)) // 상대방이 보낸 메시지만 확인
									.and(Q_CHATMESSAGE.checked.isFalse())) // 읽지 않은 메시지가 있는지 확인
								.exists()
						)
						.then(Boolean.FALSE)
						.otherwise(Boolean.TRUE),
					"isMessageRead"
				),
				Expressions.cases() // 메세지 보낸 시간
					.when(Q_CHATMESSAGE.createdAt.isNotNull())
					.then(Q_CHATMESSAGE.createdAt)
					.otherwise(Q_CHATROOM.createdAt)))
			.from(Q_CHATROOM)
			.join(Q_USER_CHATROOM).on(Q_USER_CHATROOM.chatroom.eq(Q_CHATROOM))
			.join(Q_USER).on(Q_USER_CHATROOM.user.eq(Q_USER))
			.leftJoin(Q_CHATMESSAGE).on(
				Q_CHATMESSAGE.chatroom.eq(Q_CHATROOM)
					.and(Q_CHATMESSAGE.chatMessageId.eq(
						JPAExpressions
							.select(Q_CHATMESSAGE.chatMessageId.max()) // 제일 최신 메세지 선택
							.from(Q_CHATMESSAGE)
							.where(Q_CHATMESSAGE.chatroom.eq(Q_CHATROOM))
					))
			)
			.where(Q_USER_CHATROOM.user.userId.ne(userId)
				.and(
					JPAExpressions
						.selectOne()
						.from(Q_FRIENDSHIP)
						.where(
							(Q_FRIENDSHIP.user.userId.eq(userId)
								.and(Q_FRIENDSHIP.friend.userId.eq(Q_USER.userId)))
								.or(Q_FRIENDSHIP.user.userId.eq(Q_USER.userId)
									.and(Q_FRIENDSHIP.friend.userId.eq(userId))))
						.exists()
				)
				.and(  // 자신도 해당 채팅방에 참여하고 있는지 확인
					JPAExpressions
						.selectOne()
						.from(Q_USER_CHATROOM)
						.where(Q_USER_CHATROOM.chatroom.eq(Q_CHATROOM)
							.and(Q_USER_CHATROOM.user.userId.eq(userId)))
						.exists()
				)
			) // 상대방이 보낸 메세지(자신 X)
			.orderBy( // 최신 순으로 정렬
				Expressions.cases()
					.when(Q_CHATMESSAGE.createdAt.isNotNull())
					.then(Q_CHATMESSAGE.createdAt)
					.otherwise(Q_CHATROOM.createdAt).desc()
			)
			.fetch();
		// 채팅방의 친구 id 리스트
		List<Long> userIds = chatrooms.stream()
			.map(ChatroomListResponseDto::getFriendId)
			.toList();
		Map<Long, Boolean> onlineStatus = userStatusService.getBulkOnlineStatus(userIds); // 친구들의 온라인 상태 파악
		return chatrooms.stream()
			.map(chatroom -> ChatroomListResponseDto.builder()
				.friendId(chatroom.getFriendId())
				.nickname(chatroom.getNickname())
				.profileImg(S3Util.getImgUrl(chatroom.getProfileImg()))
				.isOnline(onlineStatus.getOrDefault(chatroom.getFriendId(), false))
				.recentMessage(chatroom.getRecentMessage())
				.isMessageRead(chatroom.getIsMessageRead())
				.createdAt(chatroom.getCreatedAt())
				.build())
			.collect(Collectors.toList());
	}

	// 단일 채팅방 상세 정보
	@Override
	public Optional<ChatroomDetailResponseDto> findChatroomById(Long chatroomId, Long userId) {

		if (chatroomId == null) {
			log.info("방 Id 없음");
			return Optional.empty();
		}

		// 채팅방 기본 정보 조회
		Tuple chatroomInfo = queryFactory
			.select(
				Q_USER.userId,
				Q_USER.nickname,
				Q_USER.profileImg,
				Q_CHATROOM.createdAt
			)
			.distinct()
			.from(Q_CHATROOM)
			.join(Q_USER_CHATROOM).on(Q_USER_CHATROOM.chatroom.eq(Q_CHATROOM))
			.join(Q_USER).on(Q_USER_CHATROOM.user.eq(Q_USER))
			.where(Q_CHATROOM.chatroomId.eq(chatroomId)
				.and(Q_USER_CHATROOM.user.userId.ne(userId)))
			.fetchFirst();

		if (chatroomInfo == null) {
			log.info("방 정보 없음");
			return Optional.empty();
		}

		// 최근 60개 메시지 조회
		List<ChatMessage> recentMessages = queryFactory
			.selectFrom(Q_CHATMESSAGE)
			.join(Q_CHATMESSAGE.sender).fetchJoin()  // N+1 문제 방지
			.where(Q_CHATMESSAGE.chatroom.chatroomId.eq(chatroomId))
			.orderBy(Q_CHATMESSAGE.createdAt.asc())
			.limit(60)
			.fetch();

		// ChatMessageResponseDto로 변환
		List<ChatMessageResponseDto> messagesDtos = recentMessages.stream()
			.map(ChatMessageResponseDto::from)
			.toList();

		Boolean isOnline = userStatusService.isUserOnline(chatroomInfo.get(Q_USER.userId));

		return Optional.of(ChatroomDetailResponseDto.builder()
			.chatroomId(chatroomId)
			.friendId(chatroomInfo.get(Q_USER.userId))
			.nickname(chatroomInfo.get(Q_USER.nickname))
			.profileImg(S3Util.getImgUrl(chatroomInfo.get(Q_USER.profileImg)))
			.isOnline(isOnline)
			.messages(messagesDtos)
			.createdAt(chatroomInfo.get(Q_CHATROOM.createdAt))
			.build());
	}

	@Override
	public Optional<ChatroomDetailResponseDto> findExistingChatroom(Long userId, Long friendId) {
		log.info("유저 {}, 친구 {}", userId, friendId);
		Long chatroomId = queryFactory
			.select(Q_CHATROOM.chatroomId)
			.from(Q_CHATROOM)
			.where(
				JPAExpressions
					.selectFrom(Q_USER_CHATROOM)
					.where(
						Q_USER_CHATROOM.chatroom.eq(Q_CHATROOM)
							.and(Q_USER_CHATROOM.user.userId.eq(userId))
					).exists()
					.and(
						JPAExpressions
							.selectFrom(Q_USER_CHATROOM)
							.where(
								Q_USER_CHATROOM.chatroom.eq(Q_CHATROOM)
									.and(Q_USER_CHATROOM.user.userId.eq(friendId))
							).exists()
					)
			)
			.fetchFirst();
		log.info("방 번호 {}", chatroomId);
		return findChatroomById(chatroomId, userId);
	}
}
