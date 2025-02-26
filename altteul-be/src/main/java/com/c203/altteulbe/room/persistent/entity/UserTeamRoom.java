package com.c203.altteulbe.room.persistent.entity;

import org.springframework.data.domain.Persistable;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserTeamRoom extends BaseCreatedEntity implements Persistable<UserTeamRoomId> {
	@EmbeddedId
	private UserTeamRoomId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("teamRoomId")
	@JoinColumn(name = "team_room_id", nullable = false)
	private TeamRoom teamRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private int teamOrder;

	@Override
	public boolean isNew() {
		return super.getCreatedAt() == null;
	}

	public static UserTeamRoom create(TeamRoom teamRoom, User user, int teamOrder) {
		return UserTeamRoom.builder()
			.id(new UserTeamRoomId(teamRoom.getId(), user.getUserId()))
			.teamRoom(teamRoom)
			.user(user)
			.teamOrder(teamOrder)
			.build();
	}
}
