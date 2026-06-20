package ru.mrkotyaka.storeservice.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.dto.stores.WarehouseRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.WarehouseRsDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final StoreProcessor storeProcessor;

    @GetMapping
    public List<WarehouseRsDto> getStoreProducts() {
        log.info("REST: Getting all store products");
        return storeProcessor.getStoreProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseRsDto createProductStore(@Valid @RequestBody WarehouseRqDto request) {
        log.info("REST: Add product `{}` to store `{}`", request.productId(), request.storeId());
        return storeProcessor.createProductStore(request);
    }

    @GetMapping("/store/{storeId}")
    public List<WarehouseRsDto> getProductsByStore(@PathVariable UUID storeId) {
        log.info("REST: Getting products from store `{}`", storeId);
        return storeProcessor.getProductsByStore(storeId);
    }

    @GetMapping("/product/{productId}")
    public List<WarehouseRsDto> getStoresByProduct(@PathVariable UUID productId) {
        log.info("REST: Getting stores where exists product `{}`", productId);
        return storeProcessor.getStoresByProduct(productId);
    }

    @GetMapping("/{storeId}/{productId}")
    public WarehouseRsDto getStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Getting product `{}` from store `{}` ", productId, storeId);
        return storeProcessor.getStoreProduct(storeId, productId);
    }

    @PutMapping
    public WarehouseRsDto updateStoreProduct(@Valid @RequestBody WarehouseRqDto request) {
        log.info("REST: Updating entryType `{}`, store `{}` and product `{}`", request.entryType(), request.storeId(), request.productId());
        return storeProcessor.updateStoreProduct(request);
    }

    @DeleteMapping("/{storeId}/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStoreProduct(
            @PathVariable UUID storeId,
            @PathVariable UUID productId
    ) {
        log.info("REST: Deleting from store `{}` product `{}`", storeId, productId);
        storeProcessor.deleteStoreProduct(storeId, productId);
    }


}
