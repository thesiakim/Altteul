package com.c203.altteulbe.room.web.dto.response;

import java.util.List;
import com.c203.altteulbe.game.web.dto.response.GameStartForProblemDto;
import com.c203.altteulbe.game.web.dto.response.GameStartForTestcaseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleRoomGameStartResponseDto {
	private Long gameId;
	private Long leaderId;
	private List<SingleRoomGameStartForUserInfoResponseDto> users;
	private GameStartForProblemDto problem;
	private List<GameStartForTestcaseDto> testcases;

	public static SingleRoomGameStartResponseDto from(Long gameId, Long leaderId,
									  List<SingleRoomGameStartForUserInfoResponseDto> users,
		   							  GameStartForProblemDto problem,
									  List<GameStartForTestcaseDto> testcase) {
		return SingleRoomGameStartResponseDto.builder()
			.gameId(gameId)
			.leaderId(leaderId)
			.users(users)
			.problem(problem)
			.testcases(testcase)
			.build();
	}
}
