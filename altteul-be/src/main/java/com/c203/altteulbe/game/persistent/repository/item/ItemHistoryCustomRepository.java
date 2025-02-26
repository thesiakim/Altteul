package com.c203.altteulbe.game.persistent.repository.item;

public interface ItemHistoryCustomRepository {
	public Boolean existsUnusedItemByGameIdAndTeamIdAndItemId(Long gameId, Long teamId, Long itemId);
}
