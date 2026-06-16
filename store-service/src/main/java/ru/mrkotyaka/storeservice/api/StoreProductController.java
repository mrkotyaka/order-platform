package ru.mrkotyaka.storeservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductRsDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/store-products")
@RequiredArgsConstructor
public class StoreProductController {
    private final StoreProcessor storeProcessor;

    @PostMapping
    public ResponseEntity<StoreProductRsDto> createProductStore(@Valid @RequestBody StoreProductRqDto request) {
        log.info("REST: Add product `{}` to store `{}`", request.productId(), request.storeId());
        var result = storeProcessor.createProductStore(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<StoreProductRsDto>> getStoreProducts() {
        log.info("REST: Getting all store products");
        var result = storeProcessor.getStoreProducts();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StoreProductRsDto>> getProductsByStore(@PathVariable UUID storeId) {
        log.info("REST: Getting products for store `{}`", storeId);
        var result = storeProcessor.getProductsByStore(storeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StoreProductRsDto>> getStoresByProduct(@PathVariable UUID productId) {
        log.info("REST: Getting stores for product `{}`", productId);
        var result = storeProcessor.getStoresByProduct(productId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<StoreProductRsDto> getStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Getting store `{}` product `{}`", storeId, productId);
        var result = storeProcessor.getStoreProduct(storeId, productId);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<StoreProductRsDto> updateStoreProduct(@Valid @RequestBody StoreProductRqDto request) {
        log.info("REST: Updating store `{}` product `{}`", request.storeId(), request.productId());
        var result = storeProcessor.updateStoreProduct(request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{storeId}/{productId}")
    public ResponseEntity<Void> deleteStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Deleting store `{}` product `{}`", storeId, productId);
        storeProcessor.deleteStoreProduct(storeId, productId);
        return ResponseEntity.noContent().build();
    }
}
