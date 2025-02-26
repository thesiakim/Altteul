package com.c203.altteulbe.game.persistent.entity.item;

import java.util.ArrayList;
import java.util.List;

import com.c203.altteulbe.common.entity.BaseCreatedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseCreatedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_id", columnDefinition = "INT UNSIGNED", nullable = false)
	private Long id;

	@Column(name = "item_name", columnDefinition = "VARCHAR(100)", nullable = false)
	private String itemName;

	@Column(name = "item_content", columnDefinition = "VARCHAR(255)", nullable = false)
	private String itemContent;

	@OneToMany(mappedBy = "item")
	private List<ItemHistory> itemHistories = new ArrayList<>();
}