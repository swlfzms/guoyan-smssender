package com.game.repository;

import com.game.entity.ActivityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityResultRepository extends JpaRepository<ActivityResult, Long> {
    public ActivityResult findByPhoneAndActivityCodeAndDeleted(@Param("phone") String phone, @Param("activityCode") String activityCode, @Param("deleted") boolean deleted);
}
