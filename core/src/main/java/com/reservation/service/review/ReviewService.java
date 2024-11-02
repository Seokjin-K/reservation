package com.reservation.service.review;

import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.reservation.ReservationStatus;
import com.reservation.entity.review.Rating;
import com.reservation.entity.review.ReviewEntity;
import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import com.reservation.exception.NonExistReservationException;
import com.reservation.exception.extend.*;
import com.reservation.repository.reservation.ReservationRepository;
import com.reservation.repository.review.ReviewRepository;
import com.reservation.repository.store.StoreRepository;
import com.reservation.review.ReviewRequest;
import com.reservation.review.ReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    /**
     * 새로운 리뷰를 작성
     * 예약 상태가 'VISITED' 인 경우이고, 아직 매장에 리뷰를 안 쓴 경우
     * 해당 매장의 평균 별점 업데이트
     *
     * @param userEntity 리뷰 작성자
     * @param request    리뷰 작성 요청 정보
     * @return 작성된 리뷰 정보
     * @throws NonExistReservationException 리뷰 작성 자격이 없거나,
     *                                      이미 작성한 경우
     */
    public ReviewResponse createReview(
            UserEntity userEntity, ReviewRequest request) {
        // 예약 정보 조회
        ReservationEntity reservationEntity = this.reservationRepository
                .findById(request.getReservationId())
                .orElseThrow(NonExistReservationException::new);

        // 리뷰 자격 검증
        validateReviewEligibility(userEntity, reservationEntity);
        // 중복 리뷰 검증
        validateDuplicateReview(reservationEntity.getId());

        // 리뷰 정보 저장
        ReviewEntity reviewEntity = this.reviewRepository.save(
                buildReviewEntity(userEntity, reservationEntity, request)
        );
        // 매장의 평균 평점 업데이트
        updateStoreAverageRating(reviewEntity.getStoreEntity().getId());

        log.info("\u001B[32mcreate review  -> {}",
                reviewEntity.getId() + "\u001B[0m");
        return ReviewResponse.from(reviewEntity);
    }

    /**
     * 리뷰 작성 자격 검증
     * - 예약자 본인인지 확인
     * - 방문 완료(VISITED) 상태인지 확인
     *
     * @param userEntity        리뷰 작성자
     * @param reservationEntity 예약 정보
     */
    private void validateReviewEligibility(
            UserEntity userEntity, ReservationEntity reservationEntity) {

        if (!reservationEntity.getUserEntity().getId()
                .equals(userEntity.getId())) {
            throw new NotValidReservationException();
        }

        if (reservationEntity.getReservationStatus() !=
                ReservationStatus.VISITED) {
            throw new InvalidReservationStatusException();
        }
    }

    /**
     * 중복 리뷰 검증
     * 하나의 예약당 하나의 리뷰만 작성 가능
     */
    private void validateDuplicateReview(Long reservationId) {
        if (this.reviewRepository.existsByReservationEntity_Id(reservationId)) {
            throw new AlreadyExistReviewException();
        }
    }

    /**
     * 매장의 평균 평점 업데이트
     */
    private void updateStoreAverageRating(Long storeId) {
        Double rating =
                this.reviewRepository.calculateStoreAverageRating(storeId);

        StoreEntity storeEntity = this.storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        // 현재 데이터베이스의 review 평점은 TINYINT 사용을 위해 0부터 10의 값이 사용됨
        // 그러나 ReviewRequest, ReviewResponse, StoreEntity 는 0~5의 값을 사용
        // 이 때문에 원래의 평균에서 2를 나눔
        storeEntity.updateRating(rating / 2);
        log.info("\u001B[32mrating update  -> {}", rating + "\u001B[0m");
    }

    private ReviewEntity buildReviewEntity(
            UserEntity userEntity,
            ReservationEntity reservationEntity,
            ReviewRequest request
    ) {
        Rating rating = Rating.fromRating(request.getRating());

        if (rating == null) {
            throw new InvalidReviewRatingException();
        }

        return ReviewEntity.builder()
                .userEntity(userEntity)
                .storeEntity(reservationEntity.getStoreEntity())
                .reservationEntity(reservationEntity)
                .content(request.getContent())
                .rating(rating.getValue())
                .build();
    }

}