package com.c203.altteulbe.game.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.response.PageResponse;
import com.c203.altteulbe.common.utils.PaginateUtil;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.repository.game.GameCustomRepository;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.service.exception.GameNotParticipatedException;
import com.c203.altteulbe.game.web.dto.record.response.GameRecordResponseDto;
import com.c203.altteulbe.game.web.dto.record.response.ItemInfo;
import com.c203.altteulbe.game.web.dto.record.response.ProblemInfo;
import com.c203.altteulbe.game.web.dto.record.response.TeamInfo;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.service.exception.NotFoundUserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GameHistoryService {

	private final GameRepository gameRepository;
	private final UserRepository userRepository;
	public PageResponse<GameRecordResponseDto> getGameRecord(Long userId, Pageable pageable) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundUserException();
		}
		List<Game> games = gameRepository.findWithItemAndProblemAndAllMemberByUserId(userId);

		List<Game> pagedGames = games.stream()
			.sorted(Comparator.comparing(Game::getCreatedAt).reversed()) // 최신순 정렬
			.skip(pageable.getOffset()) // Offset 만큼 건너뛰기
			.limit(pageable.getPageSize()) // 한 페이지에 필요한 개수만큼 가져오기
			.toList();


		List<GameRecordResponseDto> gameRecordResponseDtos = new ArrayList<>();
 		for (Game game: pagedGames) {
			ProblemInfo problemInfo = ProblemInfo.builder()
				.problemId(game.getProblem().getId())
				.description(game.getProblem().getDescription())
				.problemTitle(game.getProblem().getProblemTitle())
				.build();

			List<TeamInfo> teamInfos = extractTeamInfos(game);

			TeamInfo myTeam = teamInfos.stream()
				.filter(teamInfo -> teamInfo.getMembers().stream()
					.anyMatch(member -> member.getUserId().equals(userId))) // `anyMatch()`로 검사
				.findFirst()
				.orElseThrow(GameNotParticipatedException::new);

			// 모든 해당 팀을 리스트로 변환 후 추가
			List<TeamInfo> opponents = new ArrayList<>(teamInfos.stream()
				.filter(teamInfo -> teamInfo.getMembers().stream()
					.noneMatch(member -> member.getUserId().equals(userId))) // 조건 만족하는 모든 팀 필터링
				.toList());


			List<ItemInfo> items;
			if (myTeam != null) items = ItemInfo.from(game, myTeam.getTeamId());
			else throw new NullPointerException();
			gameRecordResponseDtos.add(GameRecordResponseDto.from(game, problemInfo, items, myTeam, opponents));
		}

		Page<GameRecordResponseDto> paginateResult = PaginateUtil.paginate(gameRecordResponseDtos,
			pageable.getPageNumber(), pageable.getPageSize());
		return new PageResponse<>("games", paginateResult);
	}

	private static List<TeamInfo> extractTeamInfos(Game game) {
		if (game.getBattleType() == BattleType.S) {
			// 개인방 정보 변환 (TeamInfo 형식으로 변환)
			return game.getSingleRooms().stream()
				.map(TeamInfo::fromSingleRoom)
				.toList();
		} else {
			// 팀방 정보 변환
			return game.getTeamRooms().stream()
				.map(TeamInfo::fromTeamRoom)
				.toList();
		}
	}
}
