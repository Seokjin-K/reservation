package com.reservation.controller;

import com.reservation.entity.user.UserEntity;
import com.reservation.review.ReviewRequest;
import com.reservation.review.ReviewResponse;
import com.reservation.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.h2.engine.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     * @param userEntity 리뷰를 작성하는 회원의 엔티티
     * @param request 리뷰의 정보를 담은 요청
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserEntity userEntity,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(
                this.reviewService.createReview(userEntity, request)
        );
    }
}
