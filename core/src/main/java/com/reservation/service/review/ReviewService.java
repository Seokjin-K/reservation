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
     * 1. 리뷰 자격 확인
     * 2. 평점이 0.5점 단위인지 확인
     * 3. 중복 리뷰 확인
     * 5. 리뷰 정보 저장
     * 6. 해당 매장의 평균 별점 업데이트
     *
     * @param userEntity 리뷰 작성자의 엔티티
     * @param request    리뷰 작성 요청 정보
     */
    public ReviewResponse createReview(
            UserEntity userEntity, ReviewRequest request) {
        // 예약 정보 조회
        ReservationEntity reservationEntity = this.reservationRepository
                .findById(request.getReservationId())
                .orElseThrow(NonExistReservationException::new);
        
        // rating 단위 검증
        isValidRatingIncrement(request.getRating());
        // 리뷰 자격 검증
        validateReviewEligibility(userEntity, reservationEntity);
        // 중복 리뷰 검증
        validateDuplicateReview(reservationEntity.getId());

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
     * 리뷰 수정
     * 1. 유효한 리뷰인지 확인
     * 2. 평점이 0.5점 단위인지 확인
     * 2. 리뷰 업데이트
     * 3. 해당 매장의 평점 업데이트
     * 
     * @param userEntity 리뷰를 수정하려는 작성자의 엔티티
     * @param reviewId 수정하려는 리뷰의 id
     * @param request 수정하려는 정보를 담은 요청
     */
    public ReviewResponse updateReview(
            UserEntity userEntity, Long reviewId, ReviewRequest request) {

        // 유효한 리뷰인지 확인
        ReviewEntity reviewEntity = validateReview(reviewId, userEntity);
        // 평점이 0.5점 단위인지 확인
        isValidRatingIncrement(request.getRating());

        // 리뷰 정보 업데이트
        reviewEntity.update(request.getContent(), request.getRating());
        // 리뷰를 작성한 매장의 평균 평점 업데이트
        updateStoreAverageRating(reviewEntity.getStoreEntity().getId());

        return ReviewResponse.from(reviewEntity);
    }

    /**
     * 리뷰 삭제
     * 1. 리뷰 자격 확인
     * 2. 리뷰 삭제
     * 3. 리뷰를 작성했던 매장의 평균 평점 재설정
     */
    public Long deleteReview(UserEntity userEntity, Long reviewId) {
        ReviewEntity reviewEntity = validateReview(reviewId, userEntity);
        this.reviewRepository.delete(reviewEntity);
        updateStoreAverageRating(reviewEntity.getStoreEntity().getId());
        return reviewId;
    }

    /**
     * 리뷰 작성 자격 검증
     * 1. 예약자 본인인지 확인
     * 2. 방문 완료(VISITED) 상태인지 확인
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
     *
     * @param reservationId 해당 예약의 id
     */
    private void validateDuplicateReview(Long reservationId) {
        if (this.reviewRepository.existsByReservationEntity_Id(reservationId)) {
            throw new AlreadyExistReviewException();
        }
    }

    /**
     * 매장의 평균 평점 업데이트
     *
     * @param storeId 리뷰를 작성한 매장의 id
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

    /**
     * 리뷰 엔티티를 빌드 패턴으로 생성
     * 1. rating null 체크
     * 2. 빌드 패턴으로 리뷰 엔티티 생성
     *
     * @param userEntity        리뷰를 작성한 유저의 엔티티
     * @param reservationEntity 리류를 작성할 예약 엔티티
     * @param request           작성한 리뷰의 정보를 담은 요청
     * @return 리뷰 엔티티
     */
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

    /**
     * 리뷰 조회 및 작성자 확인
     */
    private ReviewEntity validateReview(
            Long reviewId, UserEntity userEntity) {

        ReviewEntity reviewEntity = this.reviewRepository.findById(reviewId)
                .orElseThrow(NonExistReservationException::new);

        // 리뷰 작성자가 맞는지 확인
        if (!reviewEntity.getUserEntity().getId().equals(userEntity.getId())) {
            throw new NotReviewOwnerException();
        }
        return reviewEntity;
    }

    /**
     * 들어온 평점의 값이 0.5점 단위인지 학인
     */
    public void isValidRatingIncrement(Double rating) {
        if (rating == null || (rating * 10) % 5 != 0) {
            throw new NotValidRatingException();
        }
    }
}
