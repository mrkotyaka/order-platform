package ru.mrkotyaka.storeservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRsDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreProcessor storeProcessor;

    @GetMapping
    public ResponseEntity<List<StoreRsDto>> getStores() {
        log.info("REST: Getting all stores");
        List<StoreRsDto> stores = storeProcessor.getStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreRsDto> getStoreById(@PathVariable UUID id) {
        log.info("REST: Getting store by id: {}", id);
        StoreRsDto store = storeProcessor.getStoreById(id);
        return ResponseEntity.ok(store);
    }

    @PostMapping
    public ResponseEntity<StoreRsDto> createStore(@Valid @RequestBody StoreRqDto request) {
        log.info("REST: Creating new store: {}", request.name());
        StoreRsDto created = storeProcessor.createStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreRsDto> updateStore(
            @PathVariable UUID id,
            @Valid @RequestBody StoreRqDto request
    ) {
        log.info("REST: Updating store: {}", id);
        StoreRsDto updated = storeProcessor.updateStore(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
        log.info("REST: Deleting store: {}", id);
        storeProcessor.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}
