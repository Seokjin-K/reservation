package com.reservation.service.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutocompleteService {

    private final Trie<String, String> trie;

    public void addAutoCompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
        log.info("\u001B[32mautocomplete add -> {}", keyword + "\u001B[0m");
    }

    public List<String> getStoreNamesByKeyword(String keyword) {
        log.info("\u001B[32mautocomplete -> {}", keyword + "\u001B[0m");
        return new ArrayList<>(this.trie.prefixMap(keyword).keySet());
    }

    public void updateAutocompleteKeyword(
            String currentKeyword, String updateKeyword) {
        this.trie.remove(currentKeyword);
        this.trie.put(updateKeyword, null);
        log.info("\u001B[32mautocomplete update {} -> {}",
                currentKeyword, updateKeyword + "\u001B[0m");
    }

    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
        log.info("\u001B[32mautocomplete remove -> {}", keyword + "\u001B[0m");
    }
}
