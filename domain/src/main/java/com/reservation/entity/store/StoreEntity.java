package com.reservation.entity.store;

import com.reservation.entity.base.BaseEntity;
import com.reservation.entity.reservation.ReservationEntity;
import com.reservation.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "STORE")
public class StoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "json") // 영업시간 - JSON 형태로 저장
    @Convert(converter = BusinessHoursConverter.class)
    private BusinessHours businessHours;

    @Column(nullable = false)
    private double rating;

    @OneToMany(mappedBy = "storeEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<ReservationEntity> reservationEntities;

    public void updateStore(
            UserEntity userEntity,
            String name,
            String address,
            String description) {

        this.userEntity = userEntity;
        this.name = name;
        this.address = address;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreEntity that = (StoreEntity) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }


}
