package com.reservation.repository.review;

import com.reservation.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByReservationEntity_Id(Long reservationId);

    // JPQL
    @Query(value = "SELECT COALESCE(AVG(rating), 0) FROM review WHERE store_id = :storeId",
            nativeQuery = true)
    Double calculateStoreAverageRating(@Param("storeId") Long storeId);
}
