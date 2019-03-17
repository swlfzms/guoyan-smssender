package com.game.repository;

import com.game.entity.ActivityReservation;
import com.game.entity.ActivityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityReservationRepository extends JpaRepository<ActivityReservation, Long> {

    public List<ActivityReservation> findByStatusAndDeleted(@Param("status") boolean status, @Param("deleted") boolean deleted);

    public ActivityReservation findByReservationCodeAndActivityCodeAndStatusAndDeleted(@Param("reservationCode") String reservationCode, @Param("activityCode") String activityCode, @Param("status") boolean status, @Param("deleted") boolean deleted);
}
