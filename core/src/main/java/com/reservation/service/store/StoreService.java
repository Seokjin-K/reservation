package com.reservation.service.store;

import com.reservation.entity.store.StoreEntity;
import com.reservation.exception.extend.AlreadyExistStoreException;
import com.reservation.exception.extend.NonExistStoreException;
import com.reservation.repository.store.StoreRepository;
import com.reservation.store.StoreRequest;
import com.reservation.store.StoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    //private final Double DEFAULT_RATING = 0.0;
    private final StoreRepository storeRepository;
    private final AutoCompleteService autoCompleteService;

    /**
     * 매장 등록
     * 1. 매장 존재 유무 체크
     * 2. 존재하면 StoreEntity 저장
     * 3. 자동완성을 위한 매장 정보 추가
     *
     * @param userId
     * @param request
     * @return StoreResponse
     */
    public StoreResponse registerStore(
            Long userId, StoreRequest request) {

        if (this.storeRepository.existsByNameAndAddress(
                request.getName(), request.getAddress())) {
            throw new AlreadyExistStoreException();
        }

        StoreEntity storeEntity = buildStoreEntity(userId, request);

        this.storeRepository.save(storeEntity);

        // 자동완성을 위한 매장 정보 추가
        //this.autoCompleteService.autocomplete();

        log.info("\u001B[32mregister store -> {}", request.getName() + "\u001B[0m");

        return StoreResponse.from(storeEntity);
    }

    /**
     * 매장의 ID로 매장 검색
     * @param storeId
     * @return StoreResponse
     */
    @Transactional(readOnly = true)
    public StoreResponse getStore(Long storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        log.info("\u001B[32mget store  -> {}", storeEntity.getName() + "\u001B[0m");

        return StoreResponse.from(storeEntity);
    }

    /**
     * 매장 삭제
     * @param userId
     * @param storeId
     */
    public String deleteStore(Long userId, Long storeId) {
        StoreEntity storeEntity = storeRepository.findByIdAndUserId(storeId, userId)
                .orElseThrow(NonExistStoreException::new);

        storeRepository.delete(storeEntity);

        log.info("\u001B[32mdelete store  -> {}", storeEntity.getName() + "\u001B[0m");
        return storeEntity.getName();
    }

    /**
     * reqeust 의 정보로 매장 정보 업데이트
     * @param userId
     * @param storeId
     * @param request
     * @return
     */
    public StoreResponse updateStore(
            Long userId, Long storeId, StoreRequest request) {

        StoreEntity storeEntity =
                storeRepository.findByIdAndUserId(storeId, userId)
                        .orElseThrow(NonExistStoreException::new);

        storeEntity.updateStore(request.getName(), request.getAddress(),
                request.getDescription());

        log.info("\u001B[32mupdate store  -> {}", storeEntity.getName() + "\u001B[0m");
        return StoreResponse.from(storeEntity);
    }

    /**
     * Builder 패턴을 이용하여 StoreEntity 생성
     * @param userId
     * @param request
     * @return StoreEntity
     */
    private StoreEntity buildStoreEntity(
            Long userId, StoreRequest request) {

        return StoreEntity.builder()
                .userId(userId)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();
    }
}
