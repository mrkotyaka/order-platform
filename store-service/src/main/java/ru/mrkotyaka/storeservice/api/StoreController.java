package ru.mrkotyaka.storeservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public List<StoreRsDto> getStores() {
        log.info("REST: Getting all stores");
        return storeProcessor.getStores();
    }

    @GetMapping("/{id}")
    public StoreRsDto getStoreById(@PathVariable UUID id) {
        log.info("REST: Getting store by id: {}", id);
        return storeProcessor.getStoreById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreRsDto createStore(@Valid @RequestBody StoreRqDto request) {
        log.info("REST: Creating new store: {}", request.name());
        return storeProcessor.createStore(request);
    }

    @PutMapping("/{id}")
    public StoreRsDto updateStore(
            @PathVariable UUID id,
            @Valid @RequestBody StoreRqDto request
    ) {
        log.info("REST: Updating store: {}", id);
        return storeProcessor.updateStore(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable UUID id) {
        log.info("REST: Deleting store: {}", id);
        storeProcessor.deleteStore(id);
    }
}
