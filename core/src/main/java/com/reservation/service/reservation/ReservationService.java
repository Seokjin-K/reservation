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

import java.util.Comparator;
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
     * 1. 존재하는 매장인지 체크
     * 2. 예약정보를 담은 엔티티 생성
     * 3. 같은 이름으로 예약한 게 있는지 체크
     * 4. 예약 저장
     *
     * @param userEntity 예약하려는 유저의 엔티티
     * @param request    요청된 예약 정보
     * @return 예약 정보 반환
     */
    public ReservationResponse createReservation(
            UserEntity userEntity, ReservationRequest request) {

        StoreEntity storeEntity =
                this.storeRepository.findById(request.getStoreId())
                        .orElseThrow(NonExistStoreException::new);

        ReservationEntity reservationEntity = buildReservationEntity(
                request, userEntity, storeEntity);

        if (storeEntity.getReservationEntities().contains(reservationEntity)) {
            throw new AlreadyExistReservationException();
        }

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
     * 예약 상태를 status 값으로 변경 (PATCH)
     * 1. 유요한 예약인지 검증
     * 2. 예약 상태를 status 의 값으로 변경
     *
     * @param userId        예약 받은 점주 회원의 엔티티
     * @param reservationId 예약 아이디
     * @param status        변경하려는 상태
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
     * 2. 예약한 매장의 점주가 로그인한 파트너 회원인지 확인
     *
     * @param userId        예약 받은 점주 회원 id
     * @param reservationId 예약 아이디
     */
    private ReservationEntity validateReservation(
            Long userId, Long reservationId) {

        ReservationEntity reservationEntity =
                this.reservationRepository.findById(reservationId)
                        .orElseThrow(NonExistReservationException::new);

        if (!Objects.equals(reservationEntity.getStoreOwnerId(), userId)) {
            throw new NotStoreOwnerException();
        }
        return reservationEntity;
    }

    /**
     * 일반 회원이 에약 'CANCELED' 상태로 변경
     * 1. 유효한 예약인지 검증
     * 2. 예약 상태 변경 (현재는 취소만)
     *
     * @param userId        예약 상태를 바꾸려는 회원의 id
     * @param reservationId 예약 상태를 바꾸려는 예약의 id
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

    /**
     * 유효한 예약인지 확인
     * 1. 예약이 존재하는지 확인
     * 2. 로그인한 일반 회원의 예약이 맞는지 확인
     *
     * @param userId        예약 상태를 바꾸려는 회원의 id
     * @param reservationId 예약 상태를 바꾸려는 예약의 id
     * @return 예약 정보를 담은 엔티티
     */
    private ReservationEntity validatePatchCustomer(
            Long userId, Long reservationId) {

        ReservationEntity reservationEntity =
                this.reservationRepository.findById(reservationId)
                        .orElseThrow(NonExistReservationException::new);

        if (!Objects.equals(reservationEntity.getUserEntity().getId(), userId)) {
            throw new NonReservationOwnerException();
        }
        return reservationEntity;
    }

    /**
     * 일반 회원의 모든 예약 가져오기
     *
     * @param userId 로그인된 회원의 id
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservation(Long userId) {

        // Security 에서 UserEntity 를 가져오고 getReservationEntities 에 접근하면
        // Laze 로딩 설정 때문에 접근이 불가.
        // 이 때문에 id만 가져와서 현재 트랜잭션에서 다시 조회.
        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(NonExistUserException::new);

        log.info("\u001B[32mget reservation customer -> {}",
                userId + "\u001B[0m");

        // Set -> List 변환하면서 정렬도 함께 처리(예약한 날짜 순서)
        return userEntity.getReservationEntities().stream()
                .sorted(Comparator.comparing(ReservationEntity::getCreatedAt)
                        .reversed())
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 로그인된 파트너 회원이 지정한 매장에 예약돼 있는 모든 정보 가져오기
     * 1. 매장 검색
     * 2. 로그인된 유저가 해당 매장의 점주인지 확인
     *
     * @param userEntity 로그인된 유저의 엔티티
     * @param storeId    확인하려는 매장의 아이디
     */
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservation(
            UserEntity userEntity, Long storeId) {

        StoreEntity storeEntity = this.storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        if (!storeEntity.getUserEntity().equals(userEntity)) {
            throw new NotStoreOwnerException();
        }

        log.info("\u001B[32mget partner reservation -> {}", storeId + "\u001B[0m");

        return storeEntity
                .getReservationEntities()
                .stream()
                .sorted(Comparator.comparing(ReservationEntity::getCreatedAt))
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 예약 10분전에 도착하여 방문 확인 진행
     * 1. 해당 매장의 예약이 존재하는지 확인
     * 2. 체크인 조건에 맞는 예약인지 확인
     * - 유효하지 않다면 상태를 'NO_SHOW'로 변경
     * 3. 유효하다면 상태를 'VISITED' 로 변경
     *
     * @param storeId 방문한 매장의 id
     * @param reservationId 확인할 예약의 id
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
