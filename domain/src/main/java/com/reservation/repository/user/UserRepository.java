package com.reservation.repository.user;

import com.reservation.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByAccount(String account);
    Optional<UserEntity> findByAccount(String account);
}
