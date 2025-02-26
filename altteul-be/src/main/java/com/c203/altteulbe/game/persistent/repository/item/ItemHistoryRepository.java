package com.c203.altteulbe.game.persistent.repository.item;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.item.ItemHistory;

public interface ItemHistoryRepository extends ItemHistoryCustomRepository, JpaRepository<ItemHistory, Long> {
}
