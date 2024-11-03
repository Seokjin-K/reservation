package com.reservation.controller;

import com.reservation.entity.user.UserEntity;
import com.reservation.review.ReviewRequest;
import com.reservation.review.ReviewResponse;
import com.reservation.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     *
     * @param userEntity 리뷰를 작성하는 유저 엔티티
     * @param request    리뷰의 정보를 담은 요청
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserEntity userEntity,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(
                this.reviewService.createReview(userEntity, request)
        );
    }

    /**
     * 리뷰 수정
     *
     * @param userEntity 작성한 리뷰의 유저 id
     * @param reviewId   작성한 리뷰의 id
     * @param request    업데이트할 정보를 담은 요청
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequest request
    ) {
        return ResponseEntity.ok(
                this.reviewService.updateReview(userEntity, reviewId, request)
        );
    }

    /**
     * 리뷰 삭제
     *
     * @param userEntity 리뷰 삭제를 요청한 회원
     * @param reviewId   삭제할 리뷰의 id
     * @return 삭제한 리뷰의 id
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Long> deleteReview(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(
                this.reviewService.deleteReview(userEntity, reviewId)
        );
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<ReviewResponse>> getStoreReviews(
            @PathVariable Long storeId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                this.reviewService.getStoreReviews(storeId, pageable)
        );
    }
}
