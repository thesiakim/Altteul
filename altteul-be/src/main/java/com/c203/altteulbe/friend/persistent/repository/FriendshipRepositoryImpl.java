package com.c203.altteulbe.friend.persistent.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.c203.altteulbe.friend.persistent.entity.Friendship;
import com.c203.altteulbe.friend.persistent.entity.QFriendship;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class FriendshipRepositoryImpl extends QuerydslRepositorySupport implements FriendshipCustomRepository {
	private final JPAQueryFactory queryFactory;
	private final QFriendship friendship = QFriendship.friendship;

	public FriendshipRepositoryImpl(JPAQueryFactory queryFactory) {
		super(Friendship.class);
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<Friendship> findAllByUserIdWithFriend(long userId, Pageable pageable) {
		JPAQuery<Friendship> query = queryFactory
			.selectFrom(friendship)
			.leftJoin(friendship.friend).fetchJoin()
			.where(friendship.id.userId.eq(userId));

		JPAQuery<Long> countQuery = queryFactory
			.select(friendship.count())
			.from(friendship)
			.where(friendship.id.userId.eq(userId));

		List<Friendship> friendships = getQuerydsl()
			.applyPagination(pageable, query)
			.fetch();

		return PageableExecutionUtils.getPage(friendships, pageable, countQuery::fetchOne);
	}

	@Override
	public boolean existsByUserAndFriend(Long userId, Long friendId) {
		return queryFactory
			.selectOne()
			.from(friendship)
			.where(friendship.user.userId.eq(userId)
				.and(friendship.friend.userId.eq(friendId)))
			.fetchFirst() != null;
	}
}
