package com.reservation.repository.store;

import com.reservation.entity.store.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {

    boolean existsByNameAndAddress(String name, String address);

    Optional<StoreEntity> findById(Long id);

    List<StoreEntity> findByNameContaining(String name);
}
