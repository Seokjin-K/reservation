package com.reservation.controller;

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
@RequestMapping("api/v1/store")
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
     * UserRole 이 PARTNER 인 회원만 허용
     * @param userId  SecurityContext 에 저장된 UserId를 가져온다.
     * @param request
     * @return ResponseEntity<StoreResponse>
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PostMapping
    public ResponseEntity<StoreResponse> registerStore(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody StoreRequest request
    ) {
        return ResponseEntity.ok(
                this.storeService.registerStore(userId, request)
        );
    }

    /**
     * 매장 상세정보 가져오기
     * @param storeName
     * @return
     */
    @GetMapping
    public ResponseEntity<List<StoreResponse>> getStore(
            @RequestParam String storeName) {
        return ResponseEntity.ok(this.storeService.getStore(storeName));
    }

    /**
     * 매장 업데이트
     * 현재 로그인된 아이디의 storeId 매장 정보 업데이트
     * @param userId
     * @param storeId
     * @param request
     * @return ResponseEntity<StoreResponse>
     */
    @PreAuthorize("hasRole('PARTNER')")
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponse> updateStore(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long storeId,
            @Valid @RequestBody StoreRequest request
    ) {
        return ResponseEntity.ok(
                this.storeService.updateStore(userId, storeId, request));
    }

    /**
     * 매장 삭제
     * 현재 로그인된 아이디의 storeId 매장 삭제
     * @param userId
     * @param storeId
     * @return 매장 이름 반환
     */
    @PreAuthorize("hasRole('PARTNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long storeId
    ) {
        return ResponseEntity.ok(this.storeService.deleteStore(userId, storeId));
    }
}
