package com.reservation.repository.reservation;

import com.reservation.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByStoreEntity_Id(Long storeId);

    Optional<ReservationEntity> findByIdAndStoreEntity_Id(
            Long reservationId, Long storeId
    );
}
