package com.reservation.controller;

import com.reservation.service.store.AutocompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autocomplete")
@RequiredArgsConstructor
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    /**
     * 자동완성
     * @param keyword
     * @return Trie 에 keyword 로 시작하는 매장 이름을 반환
     */
    @GetMapping
    public ResponseEntity<List<String>> autocompleteStore(
            @RequestParam String keyword) {
        List<String> storeNamesByKeyword =
                this.autocompleteService.getStoreNamesByKeyword(keyword);
        return ResponseEntity.ok(storeNamesByKeyword);
    }
}
