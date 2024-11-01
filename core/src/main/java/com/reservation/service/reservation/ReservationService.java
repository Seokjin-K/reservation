package com.reservation.service.reservation;

import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.reservation.ReservationStatus;
import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import com.reservation.exception.extend.AlreadyExistReservation;
import com.reservation.exception.extend.NonExistStoreException;
import com.reservation.exception.extend.NonExistUserException;
import com.reservation.repository.reservation.ReservationRepository;
import com.reservation.repository.store.StoreRepository;
import com.reservation.repository.user.UserRepository;
import com.reservation.reservation.ReservationRequest;
import com.reservation.reservation.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 생성
     * 1. 존재하는 유저인지 체크
     * 2. 존재하는 매장인지 체크
     * 3. reservationEntity 생성 및 의존관계 유지
     * 4. 같은 이름으로 예약한 게 있는지 체크
     * 5. 예약 저장
     *
     * @param userId
     * @param request
     * @return 예약 정보 반환
     */
    public ReservationResponse createReservation(
            Long userId, ReservationRequest request) {

        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(NonExistUserException::new);

        StoreEntity storeEntity =
                this.storeRepository.findById(request.getStoreId())
                .orElseThrow(NonExistStoreException::new);

        ReservationEntity reservationEntity = buildReservationEntity(
                request, userEntity, storeEntity);

        if (storeEntity.getReservationEntities().contains(reservationEntity)) {
            throw new AlreadyExistReservation();
        }

        userEntity.getReservationEntities().add(reservationEntity);
        storeEntity.getReservationEntities().add(reservationEntity);

        reservationRepository.save(reservationEntity);
        return ReservationResponse.from(reservationEntity);
    }

    /**
     * ReservationEntity 빌더 패턴으로 생성
     *
     * @param request
     * @return ReservationEntity
     */
    private ReservationEntity buildReservationEntity(
            ReservationRequest request,
            UserEntity userEntity,
            StoreEntity storeEntity) {

        return ReservationEntity.builder()
                .userEntity(userEntity)
                .storeEntity(storeEntity)
                .name(request.getName())
                .numberOfPeople(request.getNumberOfPeople())
                .reservationTime(request.getReservationTime())
                .reservationStatus(ReservationStatus.PENDING)
                .build();
    }
}
