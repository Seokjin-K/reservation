package com.reservation.review;

import com.reservation.entity.review.ReviewEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private Double rating;

    public static ReviewResponse from(ReviewEntity reviewEntity) {
        return ReviewResponse.builder()
                .reviewId(reviewEntity.getId())
                .content(reviewEntity.getContent())
                .rating(reviewEntity.getRating())
                .build();
    }
}
