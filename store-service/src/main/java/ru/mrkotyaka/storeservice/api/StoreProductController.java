package ru.mrkotyaka.storeservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public List<StoreProductRsDto> getStoreProducts() {
        log.info("REST: Getting all store products");
        return storeProcessor.getStoreProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreProductRsDto createProductStore(@Valid @RequestBody StoreProductRqDto request) {
        log.info("REST: Add product `{}` to store `{}`", request.productId(), request.storeId());
        return storeProcessor.createProductStore(request);
    }

    @GetMapping("/store/{storeId}")
    public List<StoreProductRsDto> getProductsByStore(@PathVariable UUID storeId) {
        log.info("REST: Getting products for store `{}`", storeId);
        return storeProcessor.getProductsByStore(storeId);
    }

    @GetMapping("/product/{productId}")
    public List<StoreProductRsDto> getStoresByProduct(@PathVariable UUID productId) {
        log.info("REST: Getting stores for product `{}`", productId);
        return storeProcessor.getStoresByProduct(productId);
    }

    @GetMapping("/{storeId}/{productId}")
    public StoreProductRsDto getStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Getting store `{}` product `{}`", storeId, productId);
        return storeProcessor.getStoreProduct(storeId, productId);
    }

    @PutMapping
    public StoreProductRsDto updateStoreProduct(@Valid @RequestBody StoreProductRqDto request) {
        log.info("REST: Updating store `{}` product `{}`", request.storeId(), request.productId());
        return storeProcessor.updateStoreProduct(request);
    }

    @DeleteMapping("/{storeId}/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Deleting store `{}` product `{}`", storeId, productId);
        storeProcessor.deleteStoreProduct(storeId, productId);
    }
}
