package com.reservation.repository.store;

import com.reservation.entity.store.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    boolean existsByNameAndAddress(String name, String address);

    Optional<StoreEntity> findByIdAndUserId(Long id, Long userId);
}
