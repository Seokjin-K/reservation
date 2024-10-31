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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final AutocompleteService autoCompleteService;

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
        this.autoCompleteService.addAutoCompleteKeyword(storeEntity.getName());

        log.info("\u001B[32mregister store -> {}", request.getName()
                + "\u001B[0m");
        return StoreResponse.from(storeEntity);
    }

    /**
     * 매장의 이름으로 매장 검색
     * 일부분 일치하는 매장의 정보 모두 반환
     *
     * @param storeName
     * @return StoreResponse
     */
    @Transactional(readOnly = true)
    public List<StoreResponse> getStore(String storeName) {
        List<StoreEntity> storeEntityList =
                storeRepository.findByNameContaining(storeName);

        log.info("\u001B[32mget store -> {}", storeName + "\u001B[0m");

        return storeEntityList.stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * reqeust 의 정보로 매장 정보 업데이트
     *
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

        this.autoCompleteService.updateAutocompleteKeyword(
                storeEntity.getName(), request.getName());

        storeEntity.updateStore(request.getName(), request.getAddress(),
                request.getDescription());

        log.info("\u001B[32mupdate store  -> {}", storeEntity.getName()
                + "\u001B[0m");
        return StoreResponse.from(storeEntity);
    }

    /**
     * 매장 삭제
     *
     * @param userId
     * @param storeId
     */
    public String deleteStore(Long userId, Long storeId) {
        StoreEntity storeEntity = storeRepository.findByIdAndUserId(storeId, userId)
                .orElseThrow(NonExistStoreException::new);

        storeRepository.delete(storeEntity);
        this.autoCompleteService.deleteAutocompleteKeyword(storeEntity.getName());

        log.info("\u001B[32mdelete store  -> {}", storeEntity.getName()
                + "\u001B[0m");
        return storeEntity.getName();
    }

    /**
     * Builder 패턴을 이용하여 StoreEntity 생성
     *
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
