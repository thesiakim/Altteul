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
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder
public class TodayRanking extends BaseCreatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ranking_id")
	Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tier_id", nullable = false)
	private Tier tier;

	private int ranking;  // 순위

	@Column(name = "ranking_point")
	Long rankingPoint;

	@Column(name = "ranking_change")
	Long rankingChange;

	public static TodayRanking create(User user, Tier tier, Long rankingPoint, Long rankingChange, int ranking) {
		return TodayRanking.builder()
						   .user(user)
						   .tier(tier)
						   .rankingPoint(rankingPoint)
						   .rankingChange(rankingChange)
						   .ranking(ranking)
						   .build();
	}
}
