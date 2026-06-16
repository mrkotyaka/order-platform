package ru.mrkotyaka.storeservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.stores.*;
import ru.mrkotyaka.storeservice.domain.db.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreProcessor {
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StoreProductRepository storeProductRepository;
    private final StoreMapper storeMapper;
    private final ProductMapper productMapper;
    private final StoreProductMapper storeProductMapper;

    @Transactional(readOnly = true)
    public List<StoreRsDto> getStores() {
        log.info("Getting all stores");
        var stores = storeRepository.findAll();

        if (stores.isEmpty()) {
            log.info("No stores found");
            return List.of();
        }

        log.info("Found {} stores", stores.size());
        return stores.stream()
                .map(storeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductRsDto> getProducts() {
        log.info("Getting all saved products");
        var products = productRepository.findAll();

        if(products.isEmpty()) {
            log.info("No products found");
            return List.of();
        }
        log.info("Found {} products", products.size());
        return products.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreRsDto getStoreById(UUID id) {
        log.info("Getting store by id: {}", id);
        var store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Store `%s` not found".formatted(id)));
        return storeMapper.toDto(store);
    }

    @Transactional
    public StoreRsDto createStore(StoreRqDto request) {
        log.info("Creating new store `{}`", request.name());

        if (storeRepository.existsByName(request.name())) {
            log.warn("Store `{}` already exists", request.name());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Store `%s` already exists".formatted(request.name()));
        }

        var entity = storeMapper.toEntity(request);
        var saved = storeRepository.save(entity);

        log.info("Store `{}` created successfully. Id `{}`", saved.getName(), saved.getId());
        return storeMapper.toDto(saved);
    }

    @Transactional
    public List<ProductRsDto> createProduct(List<ProductRqDto> request) {
        log.info("Creating new products from list of {} items", request.size());

        List<ProductRqDto> newProducts = request.stream()
                .filter(product -> !productRepository.existsByName(product.name()))
                .toList();

        log.info("Found {} new products to create", newProducts.size());

        List<ProductEntity> savedProducts = new ArrayList<>();
        for (ProductRqDto productDto : newProducts) {
            ProductEntity entity = productMapper.toEntity(productDto);
            ProductEntity saved = productRepository.save(entity);
            savedProducts.add(saved);
            log.debug("Created product: {}", saved.getName());
        }

        return savedProducts.stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional
    public StoreRsDto updateStore(UUID id, StoreRqDto request) {
        log.info("Updating store `{}`", id);

        var entity = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Store `%s` not found".formatted(id)));

        entity.setName(request.name());
        entity.setAddress(request.address());
        entity.setRating(request.rating());

        var updated = storeRepository.save(entity);
        log.info("Store `{}` updated", updated.getName());

        return storeMapper.toDto(updated);
    }

    @Transactional
    public void deleteStore(UUID id) {
        log.info("Deleting store `{}`", id);

        if (!storeRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Store `%s` not found".formatted(id));
        }

        storeRepository.deleteById(id);
        log.info("Store `{}` deleted", id);
    }

    @Transactional
    public StoreProductRsDto createProductStore(StoreProductRqDto request) {
        log.info("Adding product `{}` to store `{}`", request.productId(), request.storeId());

        var store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Store `%s` not found".formatted(request.storeId())));

        var product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product `%s` not found".formatted(request.productId())));

        var id = StoreProductId.builder()
                .storeId(request.storeId())
                .productId(request.productId())
                .build();

        if (storeProductRepository.existsById(id)) {
            log.warn("Product `{}` already exists in store `{}`", request.productId(), request.storeId());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product `%s` already exists in this store `%s`".formatted(request.productId(), request.storeId()));
        }

        var storeProduct = StoreProductEntity.builder()
                .id(id)
                .store(store)
                .product(product)
                .price(request.price())
                .stock(request.stock())
                .build();

        var saved = storeProductRepository.save(storeProduct);
        log.info("Product `{}` added to store successfully", saved.getId());

        return storeProductMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<StoreProductRsDto> getStoreProducts() {
        log.info("Getting all store products");
        return storeProductRepository.findAll()
                .stream()
                .map(storeProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StoreProductRsDto> getProductsByStore(UUID storeId) {
        log.info("Getting products for store `{}`", storeId);

        if (!storeRepository.existsById(storeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Store `%s` not found".formatted(storeId));
        }

        return storeProductRepository.findByStoreId(storeId)
                .stream()
                .map(storeProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StoreProductRsDto> getStoresByProduct(UUID productId) {
        log.info("Getting stores for product `{}`", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product `%s` not found".formatted(productId));
        }

        return storeProductRepository.findByProductId(productId)
                .stream()
                .map(storeProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StoreProductRsDto getStoreProduct(UUID storeId, UUID productId) {
        log.info("Getting store `{}` product `{}`", storeId, productId);
        var entity = validateStoreProduct(storeId, productId);
        return storeProductMapper.toDto(entity);
    }

    @Transactional
    public StoreProductRsDto updateStoreProduct(StoreProductRqDto request) {
        log.info("Updating store `{}` product `{}`", request.storeId(), request.productId());

        var entity = validateStoreProduct(request.storeId(), request.productId());

        entity.setPrice(request.price());
        entity.setStock(request.stock());

        var updated = storeProductRepository.save(entity);
        log.info("Store `{}` product `{}` updated", request.storeId(), request.productId());

        return storeProductMapper.toDto(updated);
    }

    @Transactional
    public void deleteStoreProduct(UUID storeId, UUID productId) {
        log.info("Deleting store `{}` product `{}`", storeId, productId);

        var id = StoreProductId.builder()
                .storeId(storeId)
                .productId(productId)
                .build();

        if (!storeProductRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product `%s` not found in store `%s`".formatted(storeId, productId));
        }

        storeProductRepository.deleteById(id);
        log.info("Store `{}` product `{}` deleted", storeId, productId);
    }

    @NonNull
    private StoreProductEntity validateStoreProduct(UUID storeId, UUID productId) {
        var id = StoreProductId.builder()
                .storeId(storeId)
                .productId(productId)
                .build();

        return storeProductRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product `%s` not found in store `%s`".formatted(storeId, productId)));
    }
}
