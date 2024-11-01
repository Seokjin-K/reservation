package com.reservation.store;

import com.reservation.entity.store.StoreEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreResponse {
    private Long id;
    private String name;
    private String address;
    private String description;
    private double rating;

    public static StoreResponse from(StoreEntity storeEntity) {
        return StoreResponse.builder()
                .id(storeEntity.getId())
                .name(storeEntity.getName())
                .address(storeEntity.getAddress())
                .description(storeEntity.getDescription())
                .rating(storeEntity.getRating())
                .build();
    }
}
