package com.reservation.review;

import com.reservation.entity.review.ReviewEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String content;
    private Double rating;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public static ReviewResponse from(ReviewEntity reviewEntity) {
        return ReviewResponse.builder()
                .reviewId(reviewEntity.getId())
                .content(reviewEntity.getContent())
                .rating(reviewEntity.getRating())
                .createAt(reviewEntity.getCreatedAt())
                .updatedAt(reviewEntity.getUpdatedAt())
                .build();
    }
}
