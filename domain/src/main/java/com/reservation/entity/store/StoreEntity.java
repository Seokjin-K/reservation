package com.reservation.entity.store;

import com.reservation.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "STORE")
public class StoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;
    private String name;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double rating;

    public void updateStore(String name, String address, String description) {
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public void updateRating(double rating) {
        this.rating = rating;
    }
}
