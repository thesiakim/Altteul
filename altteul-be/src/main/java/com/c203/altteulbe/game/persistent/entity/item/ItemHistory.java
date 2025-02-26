package com.c203.altteulbe.game.persistent.entity.item;

import org.hibernate.annotations.ColumnDefault;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.persistent.entity.Game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "item_history")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemHistory extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_history_id", columnDefinition = "INT UNSIGNED", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;

	@Column(name = "team_room_id", columnDefinition = "INT UNSIGNED", nullable = false)
	private Long teamRoom;

	@Column(name = "user_id", columnDefinition = "INT UNSIGNED", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	@ColumnDefault("'H'")
	private Type type; // 기본값 설정

	@PrePersist
	public void prePersist() {
		if (this.type == null) {
			this.type = Type.H;  // NULL일 경우 기본값 설정
		}
	}

	public enum Type {
		U,     // 아이템 사용 (USE)
		H      // 아이템 획득 (HOLD)
	}
}
