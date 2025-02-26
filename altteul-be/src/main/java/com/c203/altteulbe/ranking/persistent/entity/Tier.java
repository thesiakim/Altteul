package com.c203.altteulbe.ranking.persistent.entity;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class Tier extends BaseCreatedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tier_id", columnDefinition = "INT UNSIGNED")
	private Long id;

	private String tierName;

	@Column(columnDefinition = "INT UNSIGNED")
	private int minPoint;

	@Column(columnDefinition = "INT UNSIGNED")
	private int maxPoint;
}
