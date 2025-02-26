package com.c203.altteulbe.ranking.persistent.repository.tier;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c203.altteulbe.ranking.persistent.entity.Tier;

public interface TierRepository extends JpaRepository<Tier, Long>, TierRepositoryCustom {
}
