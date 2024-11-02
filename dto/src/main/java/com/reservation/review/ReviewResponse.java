package com.reservation.review;

import com.reservation.entity.review.ReviewEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private double score;

    public static ReviewResponse from(ReviewEntity reviewEntity) {
        return ReviewResponse.builder()
                .reviewId(reviewEntity.getId())
                .content(reviewEntity.getContent())
                .score(reviewEntity.getScore())
                .build();
    }
}
