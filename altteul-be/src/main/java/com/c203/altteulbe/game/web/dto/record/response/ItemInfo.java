package com.c203.altteulbe.game.web.dto.record.response;

import java.util.List;
import java.util.stream.Collectors;

import com.c203.altteulbe.game.persistent.entity.Game;
import com.c203.altteulbe.game.persistent.entity.item.ItemHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemInfo {
	private Long itemId;
	private String itemName;
	public static List<ItemInfo> from(Game game, Long teamId) {
		return game.getItemHistories().stream()
			.filter(itemHistory -> itemHistory.getTeamRoom().equals(teamId)) // myTeam의 teamId로 필터링
			.map(ItemInfo::from) // ItemHistory를 ItemInfo로 변환 (가정)
			.collect(Collectors.toList());
	}
	public static ItemInfo from(ItemHistory itemHistory) {
		return ItemInfo.builder()
			.itemId(itemHistory.getId())
			.itemName(itemHistory.getItem().getItemName())
			.build();
	}
}