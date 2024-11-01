package com.reservation.repository.reservation;

import com.reservation.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository
        extends JpaRepository<ReservationEntity, Long> {
}
