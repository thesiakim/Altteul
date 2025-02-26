package com.c203.altteulbe.game.persistent.entity.problem;

import java.util.List;

import com.c203.altteulbe.common.entity.BaseCreatedAndUpdatedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Problem extends BaseCreatedAndUpdatedEntity {
	@Id
	@Column(name = "problem_id", nullable = false, updatable = false)
	private Long id;

	private String problemTitle;

	@Column(columnDefinition = "TEXT")
	private String description;
	private int point;
	private int totalCount;

	@OneToMany(mappedBy = "problem")
	private List<Testcase> testcases;
}
