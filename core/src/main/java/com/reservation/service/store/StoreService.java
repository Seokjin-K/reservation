package com.reservation.service.store;

import com.reservation.entity.store.StoreEntity;
import com.reservation.entity.user.UserEntity;
import com.reservation.exception.extend.AlreadyExistStoreException;
import com.reservation.exception.extend.NonExistStoreException;
import com.reservation.exception.extend.NonExistUserException;
import com.reservation.repository.store.StoreRepository;
import com.reservation.repository.user.UserRepository;
import com.reservation.store.StoreRequest;
import com.reservation.store.StoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final UserRepository userRepository;
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
    public StoreResponse createStore(
            Long userId, StoreRequest request) {

        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(NonExistUserException::new);

        if (this.storeRepository.existsByNameAndAddress(
                request.getName(), request.getAddress())) {
            throw new AlreadyExistStoreException();
        }

        StoreEntity storeEntity = buildStoreEntity(userEntity, request);

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
                this.storeRepository.findByNameContaining(storeName);

        log.info("\u001B[32mget store -> {}", storeName + "\u001B[0m");

        return storeEntityList.stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * request 의 정보로 매장 정보 업데이트
     * 1. userId를 통해 유효한 회원인지 검사
     * 2. storeId를 통해 유효한 매장인지 검사
     * 3. 자동완성 업데이트
     * 4. 매장 정보 업데이트
     *
     * @param storeId
     * @param request
     * @return
     */
    public StoreResponse updateStore(
            Long userId, Long storeId, StoreRequest request) {

        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(NonExistUserException::new);

        // 조회하는 순간 영속성 컨텍스트가 이 엔티티를 관리하기 시작
        StoreEntity storeEntity = storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        // 엔티티 수정 -> 영속성 컨텍스트가 변경 감지
        storeEntity.updateStore(
                userEntity,
                request.getName(),
                request.getAddress(),
                request.getDescription()

                // 트랜잭션 종료 시점에 영속성 컨텍스트는 변경된 엔티티를 감지
                // update 쿼리를 자동으로 실행
        );

        this.autoCompleteService.updateAutocompleteKeyword(
                storeEntity.getName(), request.getName());

        log.info("\u001B[32mupdate store  -> {}", storeEntity.getName()
                + "\u001B[0m");
        return StoreResponse.from(storeEntity);
    }

    /**
     * 매장 삭제
     * 1. userId와 storeId를 통해 유효한 매장인지 검사
     * 2. 매장 삭제
     * 3. 자동완성 삭제
     *
     * @param userId
     * @param storeId
     */
    public String deleteStore(Long userId, Long storeId) {

        UserEntity userEntity = this.userRepository.findById(userId)
                .orElseThrow(NonExistUserException::new);

        StoreEntity storeEntity = this.storeRepository.findById(storeId)
                .orElseThrow(NonExistStoreException::new);

        if (!userEntity.getStoreEntities().contains(storeEntity)) {
            throw new NonExistStoreException();
        }

        this.storeRepository.delete(storeEntity);
        this.autoCompleteService.deleteAutocompleteKeyword(storeEntity.getName());

        log.info("\u001B[32mdelete store  -> {}", storeEntity.getName()
                + "\u001B[0m");
        return storeEntity.getName();
    }

    /**
     * buildStoreEntity 빌더 패턴으로 생성
     *
     * @param request
     * @return StoreEntity
     */
    private StoreEntity buildStoreEntity(
            UserEntity userEntity, StoreRequest request) {

        return StoreEntity.builder()
                .userEntity(userEntity)
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();
    }
}
