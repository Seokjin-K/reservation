package com.reservation.entity.review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 평점 관리를 위한 enum
 */
@Getter
@RequiredArgsConstructor
public enum Rating {
    ZERO(0, 0.0),
    HALF(1, 0.5),
    ONE(2, 1.0),
    ONE_HALF(3, 1.5),
    TWO(4, 2.0),
    TWO_HALF(5, 2.5),
    THREE(6, 3.0),
    THREE_HALF(7, 3.5),
    FOUR(8, 4.0),
    FOUR_HALF(9, 4.5),
    FIVE(10, 5.0);

    private final int value; // DB에 저장될 값
    private final double rating; // 실제 평점 값

    /**
     * 실제 평점(0.0~5.0)을 받아서 해당하는 Rating 반환
     * @param rating 실제 평점 값
     * @return 해당하는 Rating enum
     */
    public static Rating fromRating(double rating) {
        return Arrays.stream(values())
                // 부동 소수점 비교의 정밀도 문제를 피하기 위해 compare 사용
                .filter(stream -> Double.compare(stream.rating, rating) == 0)
                .findFirst()
                .orElse(null);

        // domain 모듈에서는 아무것도 의존하지 않도록 설계하고 있기 때문에
        // 값이 없을 때 CustomException 하지 않고, 이것을 호출하는 쪽에서 체크
    }

    /**
     * DB에 저장된 값(0~10)을 받아서 해당하는 Rating 반환
     * @param value DB에 저장된 값
     * @return 해당하는 Rating enum
     */
    public static Rating fromValue(int value) {
        return Arrays.stream(values())
                .filter(stream -> stream.value == value)
                .findFirst()
                .orElse(null);
    }
}
