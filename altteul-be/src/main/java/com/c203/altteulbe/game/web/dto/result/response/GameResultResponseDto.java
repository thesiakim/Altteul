package com.c203.altteulbe.game.web.dto.result.response;

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
public class GameResultResponseDto {
	private BattleType gameType;
	private String startedAt;
	private int totalHeadCount;
	private TeamInfo submittedTeam;
	private List<TeamInfo> restTeam;

	public static GameResultResponseDto from(Game game, TeamInfo submittedTeam, List<TeamInfo> restTeam) {
		if (game.getBattleType() == BattleType.S) {
			return GameResultResponseDto.builder()
				.gameType(game.getBattleType())
				.totalHeadCount(restTeam.size() + 1)
				.startedAt(String.valueOf(game.getCreatedAt()))
				.submittedTeam(submittedTeam)
				.restTeam(restTeam)
				.build();
		} else {
			return GameResultResponseDto.builder()
				.gameType(game.getBattleType())
				.totalHeadCount(submittedTeam.getTotalHeadCount() + restTeam.get(0).getTotalHeadCount())
				.startedAt(String.valueOf(game.getCreatedAt()))
				.submittedTeam(submittedTeam)
				.restTeam(restTeam)
				.build();
		}
	}
}
