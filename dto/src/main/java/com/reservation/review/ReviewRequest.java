package com.reservation.review;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class ReviewRequest {

    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;

    @Size(max = 500, message = "리뷰 내용은 500자를 초과할 수 없습니다.")
    private String content;

    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 0, message = "평점은 0점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    private Double rating;
}
