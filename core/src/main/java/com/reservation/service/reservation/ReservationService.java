package com.reservation.service.reservation;

import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.reservation.ReservationStatus;
import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import com.reservation.exception.NonExistReservationException;
import com.reservation.exception.extend.*;
import com.reservation.repository.reservation.ReservationRepository;
import com.reservation.repository.store.StoreRepository;
import com.reservation.repository.user.UserRepository;
import com.reservation.reservation.ReservationRequest;
import com.reservation.reservation.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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

        log.info("\u001B[32mreservation create -> {}",
                reservationEntity.getName() + "\u001B[0m");
        return ReservationResponse.from(reservationEntity);
    }

    /**
     * ReservationEntity 빌더 패턴으로 생성
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

    /**
     * 예약 상태를 status 로 변경 (PATCH)
     * 1. 유요한 예약인지 검증
     * 2. 예약 상태를 status 의 값으로 변경
     */
    public ReservationResponse patchReservationCustomer(
            Long userId, Long reservationId, String status) {

        ReservationEntity reservationEntity =
                validateReservation(userId, reservationId);

        reservationEntity.patchStatus(ReservationStatus.from(status));

        log.info("\u001B[32mstatus -> {}",
                reservationEntity.getReservationStatus() + "\u001B[0m");
        return ReservationResponse.from(reservationEntity);
    }

    /**
     * 유효한 예약인지 검증
     * 1. 존재하는 예약인지 검증
     * 2. 예약한 매장과 해당 유저의 매장이 동일한지 검증
     */
    private ReservationEntity validateReservation(
            Long userId, Long reservationId) {

        ReservationEntity reservationEntity =
                this.reservationRepository.findById(reservationId)
                        .orElseThrow(NonExistReservationException::new);

        // 예약한 매장과 해당 유저의 매장이 동일한지 검증
        if (!reservationEntity.getStoreEntity().isOwnedBy(userId)) {
            throw new NotStoreOwnerException();
        }
        return reservationEntity;
    }

    /**
     * 일반 회원이 에약 'CANCELED' 상태로 변경
     * 1. 유효한 예약인지 검증
     * 2. 예약 상태 변경
     */
    public ReservationResponse patchReservationCustomer(
            Long userId, Long reservationId) {

        ReservationEntity reservationEntity =
                validatePatchCustomer(userId, reservationId);

        reservationEntity.customUserChangeStatusToCanceled();

        log.info("\u001B[32m customer status change -> {}",
                reservationEntity.getReservationStatus() + "\u001B[0m");
        return ReservationResponse.from(reservationEntity);
    }

    private ReservationEntity validatePatchCustomer(
            Long userId, Long reservationId) {

        ReservationEntity reservationEntity =
                this.reservationRepository.findById(reservationId)
                        .orElseThrow(NonExistReservationException::new);

        // 해당 유저의 예약이 맞는지 확인
        if (!Objects.equals(reservationEntity.getUserEntity().getId(), userId)) {
            throw new NonReservationOwnerException();
        }

        StoreEntity storeEntity = reservationEntity.getStoreEntity();
        // 해당 매장의 예약인지 확인
        if (!storeEntity.getReservationEntities().contains(reservationEntity)) {
            throw new NonReservationOfStoreException();
        }
        return reservationEntity;
    }

    /**
     * 해당 파트너 회원의 해당 매장에 예약돼 있는 모든 정보 가져오기
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservation(
            UserEntity userEntity, Long storeId) {

        StoreEntity storeEntity = this.storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        // 해당 매장의 주인이 맞는지 확인
        if (!storeEntity.getUserEntity().equals(userEntity)) {
            throw new NotStoreOwnerException();
        }

        List<ReservationEntity> reservationEntityList =
                this.reservationRepository.findByStoreEntity_Id(storeId);

        log.info("\u001B[32mget partner reservation -> {}", storeId + "\u001B[0m");

        return reservationEntityList.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 일반 회원의 모든 예약 가져오기
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservation(Long userId) {
        List<ReservationEntity> reservationResponseList =
                this.userRepository.findById(userId).get()
                        .getReservationEntities();

        log.info("\u001B[32mget customer reservation -> {}",
                userId + "\u001B[0m");

        return reservationResponseList.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 예약 10분전에 도착하여 방문 확인 진행
     * 1. 해당 매장의 예약인지 검증
     * 2. 체크인 조건에 맞는 예약인지 검증
     * - 유효하지 않다면 상태를 'NO_SHOW'로 변경
     * 3. 유효하다면 상태를 'VISITED' 로 변경
     */
    public ReservationResponse checkIn(Long storeId, Long reservationId) {
        ReservationEntity reservationEntity = this.reservationRepository
                .findByIdAndStoreEntity_Id(reservationId, storeId)
                .orElseThrow(NonReservationOfStoreException::new);

        // 유효한 예약인지 검증
        if (!reservationEntity.isValidReservation()) {
            reservationEntity.patchStatus(ReservationStatus.NO_SHOW);
            throw new NotValidReservationException();
        }
        reservationEntity.patchStatus(ReservationStatus.VISITED);

        log.info("\u001B[32mcheck-in -> {}", reservationId + "\u001B[0m");
        return ReservationResponse.from(reservationEntity);
    }
}
