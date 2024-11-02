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

/**
 * 매장 정보를 관리하는 엔티티
 */
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

    @Column(nullable = false)
    private Double rating;

    @OneToMany(mappedBy = "storeEntity",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<ReservationEntity> reservationEntities;

    // Put
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

    // Patch
    public void updateRating(Double rating) {
        this.rating = rating;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userEntity.getId().equals(userId);
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
