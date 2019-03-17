package com.game.repository;

import com.game.entity.ActivityResult;
import com.game.entity.AdjustData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjustDataRepository extends JpaRepository<AdjustData, Long> {
}
