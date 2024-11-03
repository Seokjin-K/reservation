package com.reservation.repository.review;

import com.reservation.entity.review.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    boolean existsByReservationEntity_Id(Long reservationId);

    @Query(value = "SELECT COALESCE(AVG(rating), 0) " +
            "FROM review " +
            "WHERE store_id = :storeId",
            nativeQuery = true)
    Double calculateStoreAverageRating(@Param("storeId") Long storeId);

    @Query(value = "SELECT * FROM Review r " +
            "WHERE r.store_id = :storeId " +
            "ORDER BY r.created_at DESC",
            nativeQuery = true)
    Page<ReviewEntity> findByStoreEntity_IdOrderByCreated_AtDesc(
            @Param("storeId") Long storeId, Pageable pageable);
}
