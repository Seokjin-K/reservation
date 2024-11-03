package com.reservation.entity.review;

import com.reservation.entity.base.BaseEntity;
import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 리뷰 정보를 관리하는 엔티티
 * 예약 완료된 사용자만 리뷰를 작성할 수 있으며, 
 * 하나의 예약당 하나의 리뷰만 작성 가능
 * 리뷰 점수는 0~5, 0.5점 단위로만 가능
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "REVIEW")
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity storeEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservationEntity;

    @Column(length = 500)
    private String content;

    @Column(nullable = false)
    private int rating;  // 0~10 사이의 정수값 (실제 평점의 2배)

    /**
     * 실제 평점값(0.0~5.0)을 반환
     */
    public double getRating() {
        return Rating.fromValue(this.rating).getRating();
    }

    /**
     * 리뷰 내용과 평점을 수정
     */
    public void update(String content, double score) {
        this.content = content;
        this.rating = Rating.fromRating(score).getValue();
    }
}
