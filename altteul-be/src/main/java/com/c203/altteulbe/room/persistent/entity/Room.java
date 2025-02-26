package com.c203.altteulbe.room.persistent.entity;

import java.time.LocalDateTime;

import com.c203.altteulbe.common.dto.BattleResult;
import com.c203.altteulbe.common.dto.Language;
import com.c203.altteulbe.common.entity.BaseCreatedEntity;
import com.c203.altteulbe.game.persistent.entity.Game;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
public class Room extends BaseCreatedEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@Column(columnDefinition = "TEXT")
	private String code;

	@Enumerated(EnumType.STRING)
	private BattleResult battleResult;

	@Builder.Default
	private Integer rewardPoint = 0;
	private String lastExecuteTime;
	private String lastExecuteMemory;

	@Enumerated(EnumType.STRING)
	private Language lang;

	private boolean activation;
	private LocalDateTime finishTime;
	private Integer solvedTestcaseCount;

	public void updateStatusByGameClear(BattleResult battleResult) {
		if (battleResult != BattleResult.FAIL) {
			// FAIL이 아닌 경우에만 포인트 부여
			this.rewardPoint += 100;  // 기본 클리어 포인트

			// 1등인 경우 승리 포인트 추가
			if (battleResult == BattleResult.FIRST) {
				this.rewardPoint += 50;
			}
		}
		this.battleResult = battleResult;
		this.activation = false;
		this.finishTime = LocalDateTime.now();
	}

	// 사이드 문제 포인트 추가 (FAIL이 아닌 경우에만)
	public void addSideProblemPoint(int count) {
		if (this.battleResult != BattleResult.FAIL) {
			this.rewardPoint += count * 20;
		}
	}

	public void updateSubmissionRecord(Integer solvedTestcaseCount, String lastExecuteTime, String lastExecuteMemory,
		String code, Language lang) {
		if (this.solvedTestcaseCount == null) {
			this.lang = lang;
			this.solvedTestcaseCount = solvedTestcaseCount;
			this.lastExecuteTime = lastExecuteTime;
			this.lastExecuteMemory = lastExecuteMemory;
			this.code = code;
		} else if (solvedTestcaseCount != null && this.solvedTestcaseCount != null) {
			if (this.solvedTestcaseCount < solvedTestcaseCount) {
				this.lang = lang;
				this.solvedTestcaseCount = solvedTestcaseCount;
				this.lastExecuteTime = lastExecuteTime;
				this.lastExecuteMemory = lastExecuteMemory;
				this.code = code;
			}
		}
	}

	public void updateStatusByGameWinWithOutSolve(BattleResult battleResult) {
		this.rewardPoint += 50;
		this.battleResult = battleResult;
		this.activation = false;
		this.finishTime = LocalDateTime.now();
	}

	public void updateStatusByGameLose(BattleResult battleResult) {
		this.battleResult = battleResult;
		this.activation = false;
		this.finishTime = LocalDateTime.now();
		// FAIL인 경우 rewardPoint는 0으로 초기화
		if (battleResult == BattleResult.FAIL) {
			this.rewardPoint = 0;
		}
	}
}
