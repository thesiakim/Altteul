package com.c203.altteulbe.game.service.result;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import com.c203.altteulbe.common.dto.BattleType;
import com.c203.altteulbe.common.dto.Language;
import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.problem.Problem;
import com.c203.altteulbe.game.persistent.repository.game.GameRepository;
import com.c203.altteulbe.game.service.exception.CodeNotFoundException;
import com.c203.altteulbe.game.service.exception.GameNotFoundException;
import com.c203.altteulbe.game.web.dto.result.request.AIFeedbackRequestDto;
import com.c203.altteulbe.game.web.dto.result.response.AIFeedbackResponse;
import com.c203.altteulbe.room.persistent.entity.Room;
import com.c203.altteulbe.room.service.exception.RoomNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AIFeedbackService {
	private final ChatModel chatModel;
	private final GameRepository gameRepository;

	public AIFeedbackResponse getEvaluation(AIFeedbackRequestDto request) {
		// 유저 프롬프트 템플릿 로드 및 변수 설정

		Game game = gameRepository.findWithRoomAndProblemByGameIdAndTeamId(request.getGameId(), request.getTeamId())
			.orElseThrow(GameNotFoundException::new);

		String userPromptTemplate;

		Room myRoom;
		if (game.getBattleType() == BattleType.S) {
			myRoom = game.getSingleRooms().stream()
				.filter(singleRoom -> singleRoom.getId().equals(request.getTeamId()))
				.findFirst()
				.orElseThrow(RoomNotFoundException::new);
		} else {
			myRoom = game.getTeamRooms().stream()
				.filter(teamRoom -> teamRoom.getId().equals(request.getTeamId()))
				.findFirst()
				.orElseThrow(RoomNotFoundException::new);
		}
		if (game.getBattleType() == BattleType.S) {
			if (myRoom.getCode() == null)
				throw new CodeNotFoundException();
			userPromptTemplate = getPrompt(game.getProblem(), myRoom);
		} else {
			if (myRoom.getCode() == null)
				throw new CodeNotFoundException();
			userPromptTemplate = getPrompt(game.getProblem(), myRoom);
		}
		Map<String, Object> variables = new HashMap<>();
		variables.put("problemDescription", game.getProblem().getDescription());
		variables.put("language", myRoom.getLang() == Language.JV ? "java" : "python");
		variables.put("userCode", myRoom.getCode());

		System.out.println(userPromptTemplate);
		userPromptTemplate = escapePrompt(userPromptTemplate);
		PromptTemplate userTemplate = new PromptTemplate(userPromptTemplate, variables);
		String userCommand = userTemplate.render();

		String systemPromptTemplate = """
			너는 알고리즘 최적화 전문가야. 내가 제공하는 코드의 시간 복잡도와 공간 복잡도를 분석하고, 더 효율적인 방법이 있다면 설명해줘. 가능한 경우 코드의 시간 복잡도를 줄이기 위한 대안을 제시해줘.
			
			형식은 다음과 같은데 마크다운 양식으로 한국어로 도출해줘:
			{
			    "feedback": [
			        {
			            "code": "%s",
			            "description": "%s"
			        }
			    ],
			    "algorithmType": ["%s"],
			    "summary": "%s"
			}
			""";

		String systemCommand = String.format(systemPromptTemplate, "O(n^2) -> O(n log n)", "버블 정렬 대신 퀵 정렬 사용",
			"정렬 알고리즘", "퀵 정렬이 더 성능이 좋음");
		// 메시지 생성
		Message userMessage = new UserMessage(userCommand);
		Message systemMessage = new SystemMessage(systemCommand);

		// API 호출
		String response = chatModel.call(userMessage, systemMessage);
		log.info(response);
		response = response.replaceAll("```", "").replace("json", "");
		return AIFeedbackResponse
			.builder()
			.content(response)
			.build();
	}

	private String getPrompt(Problem problem, Room room) {
		String mdLanguage;
		switch (room.getLang()) {
			case JV -> mdLanguage = "java";
			case PY -> mdLanguage = "python";
			default -> throw new IllegalStateException("Unexpected value: " + room.getLang());
		}
		return """
			문제 설명: %s
			유저 코드:
			```%s
			%s
			```
			위의 코드에 대해 개선할 점을 리뷰해주세요. 코드 스타일, 최적화, 버그 가능성을 포함해서 상세한 피드백을 작성해주세요.
			""".formatted(problem.getDescription(), mdLanguage, room.getCode());
	}

	private String getPrompt() {
		return """
			문제 설명: {problemDescription}
			유저 코드:
			```{language}
			{userCode}
			```
			위의 코드에 대해 개선할 점을 리뷰해주세요. 코드 스타일, 최적화, 버그 가능성을 포함해서 상세한 피드백을 작성해주세요.
			""";
	}

	private String escapePrompt(String original) {
		return original.replace("{", "{{")
			.replace("}", "}}");
	}

}
