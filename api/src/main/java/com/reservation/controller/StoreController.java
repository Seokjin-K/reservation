package com.reservation.controller;

import com.reservation.entity.user.UserEntity;
import com.reservation.service.store.StoreService;
import com.reservation.store.StoreRequest;
import com.reservation.store.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 인증 과정
     * HTTP 요청 ->
     * JwtAuthenticationFilter
     * - JWT 토큰 검증, CustomUserDetails 생성, SecurityContext 에 저장 ->
     * Controller
     * - @AuthenticationPrincipal 로 주입
     */

    /**
     * 매장 등록
     * UserId와 RequestBody 를 기반으로 매장 등록을 시도
     * UserRole 이 'PARTNER' 인 회원만 허용
     *
     * @param userEntity  SecurityContext 에 저장된 UserEntity 를 가져옴
     * @param request 생성하려는 매장 정보
     * @return ResponseEntity<StoreResponse>
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PostMapping
    public ResponseEntity<StoreResponse> createStore(
            @AuthenticationPrincipal UserEntity userEntity,
            @Valid @RequestBody StoreRequest request
    ) {
        return ResponseEntity.ok(
                this.storeService.createStore(userEntity, request)
        );
    }

    /**
     * 매장 상세정보 가져오기
     *
     * @param storeName 찾으려는 매장의 이름(또는 일부분)
     * @return ResponseEntity<List < StoreResponse>>
     */
    @GetMapping
    public ResponseEntity<List<StoreResponse>> getStore(
            @RequestParam String storeName) {
        return ResponseEntity.ok(this.storeService.getStore(storeName));
    }

    /**
     * 매장 업데이트
     * 현재 로그인된 아이디의 storeId의 매장 정보 업데이트
     *
     * @param userEntity 현재 로그인된 유저의 엔티티
     * @param storeId 업데이트하려는 매장의 id
     * @param request 업데이트할 매장의 정보
     * @return ResponseEntity<StoreResponse>
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long storeId,
            @Valid @RequestBody StoreRequest request
    ) {
        return ResponseEntity.ok(
                this.storeService.updateStore(userEntity, storeId, request));
    }

    /**
     * 매장 삭제
     * 현재 로그인된 아이디의 storeId 매장 삭제
     *
     * @param userEntity 로그인된 유저의 엔티티
     * @param storeId 삭제하려는 매장의 id
     * @return 매장 이름 반환
     */
    @PreAuthorize("hasRole('PARTNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(
            @AuthenticationPrincipal UserEntity userEntity,
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(
                this.storeService.deleteStore(userEntity, storeId)
        );
    }
}
