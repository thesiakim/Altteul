package com.c203.altteulbe.ranking.persistent.entity;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.user.persistent.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingHistory extends BaseCreatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ranking_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tier_id")
	private Tier tier;

	@Column(columnDefinition = "INT UNSIGNED")
	private Long rankingPoint;

	@Column(columnDefinition = "INT UNSIGNED")
	private int ranking;

	//@Column(columnDefinition = "INT UNSIGNED")
	private int rankingChange;   // 전날 랭킹과 당일 랭킹의 차이

	public static RankingHistory create(User user, Tier tier, Long rankingPoint,
									    int ranking, int rankingChange) {
		return RankingHistory.builder()
							 .user(user)
							 .tier(tier)
							 .rankingPoint(rankingPoint)
							 .ranking(ranking)
							 .rankingChange(rankingChange)
							 .build();
	}
}
