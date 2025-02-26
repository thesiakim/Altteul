package com.c203.altteulbe.game.persistent.repository.item;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.game.persistent.entity.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
