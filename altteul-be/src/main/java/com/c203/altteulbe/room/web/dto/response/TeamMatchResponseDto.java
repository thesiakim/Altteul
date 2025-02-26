package com.c203.altteulbe.room.web.dto.response;

import java.util.List;

import com.c203.altteulbe.game.web.dto.response.GameStartForProblemDto;
import com.c203.altteulbe.game.web.dto.response.GameStartForTestcaseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class TeamMatchResponseDto {
	private GameStartForProblemDto problem;
	private List<GameStartForTestcaseDto> testcases;
	private RoomEnterResponseDto team1;
	private RoomEnterResponseDto team2;

	public static TeamMatchResponseDto toDto(RoomEnterResponseDto team1, RoomEnterResponseDto team2,
											 GameStartForProblemDto problem, List<GameStartForTestcaseDto> testcases) {
		return TeamMatchResponseDto.builder()
								   .team1(team1)
								   .team2(team2)
								   .problem(problem)
								   .testcases(testcases)
							       .build();
	}
}
