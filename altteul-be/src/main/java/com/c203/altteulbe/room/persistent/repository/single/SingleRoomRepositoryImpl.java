package com.c203.altteulbe.room.persistent.repository.single;

import static com.c203.altteulbe.room.persistent.entity.QSingleRoom.*;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SingleRoomRepositoryImpl implements SingleRoomRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public void removeUserFromRoom(Long roomId, Long userId) {
		queryFactory
				.delete(singleRoom)
				.where(singleRoom.id.eq(roomId).and(singleRoom.user.userId.eq(userId)))
				.execute();
	}
}
