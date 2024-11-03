package com.reservation.entity.reservation;

import com.reservation.entity.base.BaseEntity;
import com.reservation.entity.review.ReviewEntity;
import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RESERVATION")
public class ReservationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity storeEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer numberOfPeople;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @OneToMany(mappedBy = "userEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<ReviewEntity> reviewEntities;

    public Long getStoreOwnerId(){
        return this.getStoreEntity().getUserEntity().getId();
    }

    public void patchStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

    public void customUserChangeStatusToCanceled() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    /**
     * 해당 로직에서는 10분 이내에 도착해서 체크인되도록 동작
     * 1. 체크인 시간이 체크인 가능 범위 안에 있는지 확인
     * 2. 예약 상태가 'APPROVED' 인지 확인
     */
    public boolean isValidReservation() {
        LocalDateTime now = LocalDateTime.now();

        // 체크인 가능 시간 범위 계산
        LocalDateTime checkInStartTime = reservationTime.minusMinutes(10);
        LocalDateTime checkInEndTime = reservationTime;

        // 현재 시간이 체크인 가능 범위 안에 있는지 확인
        boolean isWithinCheckInTime = now.isAfter(checkInStartTime) &&
                now.isBefore(checkInEndTime);

        // 디버깅용 로그
        if (now.isBefore(checkInStartTime)) {
            log.info("아직 체크인 시간이 아닙니다. 체크인 시작시간: {}",
                    checkInStartTime.format(
                            DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (now.isAfter(checkInEndTime)) {
            log.info("체크인 시간이 지났습니다. 예약시간: {}",
                    checkInEndTime.format(
                            DateTimeFormatter.ofPattern("HH:mm")));
        }

        return this.reservationStatus == ReservationStatus.APPROVED &&
                isWithinCheckInTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationEntity)) return false;
        ReservationEntity that = (ReservationEntity) o;
        return id != null && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
