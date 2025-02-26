package com.c203.altteulbe.game.web.dto.record.response;

import java.util.List;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.game.persistent.entity.Game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GameRecordResponseDto {
	private BattleType gameType;
	private String startedAt;
	private int totalHeadCount;
	private List<ItemInfo> items;
	private ProblemInfo problem;
	private TeamInfo myTeam;
	private List<TeamInfo> opponents;

	public static GameRecordResponseDto from(Game game, ProblemInfo problem, List<ItemInfo> items, TeamInfo myTeam, List<TeamInfo> opponents) {
		if (game.getBattleType() == BattleType.S) {
			return GameRecordResponseDto.builder()
					.gameType(game.getBattleType())
					.totalHeadCount(opponents.size()+1)
					.startedAt(String.valueOf(game.getCreatedAt()))
					.problem(problem)
					.myTeam(myTeam)
					.opponents(opponents)
					.build();
		}
		else {
			return GameRecordResponseDto.builder()
					.gameType(game.getBattleType())
					.totalHeadCount(myTeam.getTotalHeadCount() + opponents.get(0).getTotalHeadCount())
					.startedAt(String.valueOf(game.getCreatedAt()))
					.problem(problem)
					.myTeam(myTeam)
					.items(items)
					.opponents(opponents)
					.build();
		}
	}
}
