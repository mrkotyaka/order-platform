package ru.mrkotyaka.storeservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRsDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.commonlibs.dto.order.PriceRequestDto;
import ru.mrkotyaka.commonlibs.dto.stores.*;
import ru.mrkotyaka.commonlibs.enums.store.WarehouseEntryStatus;
import ru.mrkotyaka.storeservice.domain.db.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreProcessor {
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StoreMapper storeMapper;
    private final ProductMapper productMapper;
    private final WarehouseMapper warehouseMapper;

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

        if (products.isEmpty()) {
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
    public WarehouseRsDto createProductStore(WarehouseRqDto request) {
        log.info("Adding product `{}` to store `{}`", request.productId(), request.storeId());

        log.info("Validate store `{}`", request.storeId());
        var store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Store `%s` not found".formatted(request.storeId())));
        log.info("Validate store `{}` is done", request.storeId());

        log.info("Validate product `{}`", request.productId());
        var product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product `%s` not found".formatted(request.productId())));
        log.info("Validate product `{}` is done", request.productId());

        var id = WarehouseId.builder()
                .storeId(request.storeId())
                .productId(request.productId())
                .build();

        if (warehouseRepository.existsById(id)) {
            log.warn("Product `{}` from store `{}` already exists", request.productId(), request.storeId());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Product `%s` from store `%s` already exists".formatted(request.productId(), request.storeId()));
        }

        var storeProduct = WarehouseEntity.builder()
                .id(id)
                .entryType(request.entryType())
                .store(store)
                .product(product)
                .price(request.price())
                .stock(request.stock())
                .build();

        var saved = warehouseRepository.save(storeProduct);
        log.info("Product `{}` from store `{}` added successfully", saved.getId(), saved.getProduct().getId());

        return warehouseMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseRsDto> getStoreProducts() {
        log.info("Getting all store products");
        return warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseRsDto> getProductsByStore(UUID storeId) {
        log.info("Getting products from store `{}`", storeId);

        if (!storeRepository.existsById(storeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Store `%s` not found".formatted(storeId));
        }

        return warehouseRepository.findByStoreId(storeId)
                .stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseRsDto> getStoresByProduct(UUID productId) {
        log.info("Getting stores for product `{}`", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product `%s` not found".formatted(productId));
        }

        return warehouseRepository.findByProductId(productId)
                .stream()
                .map(warehouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseRsDto getStoreProduct(UUID storeId, UUID productId) {
        log.info("Getting product `{}` from store `{}` ", productId, storeId);
        var entity = validateStoreProduct(storeId, productId);
        return warehouseMapper.toDto(entity);
    }

    @Transactional
    public WarehouseRsDto updateStoreProduct(WarehouseRqDto request) {
        log.info("Start updating entryType `{}`, store `{}` and product `{}`", request.entryType(), request.storeId(), request.productId());

        var entity = validateStoreProduct(request.storeId(), request.productId());

        entity.setEntryType(request.entryType());
        entity.setPrice(request.price());
        entity.setStock(request.stock());

        var updated = warehouseRepository.save(entity);
        log.info("EntryType `{}`, store `{}` and product `{}` was updated", request.entryType(), request.storeId(), request.productId());

        return warehouseMapper.toDto(updated);
    }

    @Transactional
    public void deleteStoreProduct(UUID storeId, UUID productId) {
        log.info("Deleting store `{}` product `{}`", storeId, productId);

        var id = WarehouseId.builder()
                .storeId(storeId)
                .productId(productId)
                .build();

        if (!warehouseRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product `%s` not found in store `%s`".formatted(storeId, productId));
        }

        warehouseRepository.deleteById(id);
        log.info("Store `{}` product `{}` deleted", storeId, productId);
    }

    @NonNull
    private WarehouseEntity validateStoreProduct(UUID storeId, UUID productId) {
        var id = WarehouseId.builder()
                .storeId(storeId)
                .productId(productId)
                .build();

        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product `%s` not found in store `%s`".formatted(storeId, productId)));
    }

    @Transactional
    public Set<OrderItemRsDto> getItemPrice(PriceRequestDto request) {
        log.warn("Start getting price");
        Set<OrderItemRsDto> orderItemRsDtos = new HashSet<>();

        for (var item : request.items()) {
            BigDecimal itemPrice = warehouseRepository.findByStoreNameAndProductName(request.storeName(), item.name(), item.quantity());
            BigDecimal itemQuantity = BigDecimal.valueOf(item.quantity());

            if (itemPrice == null) throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item `%s` not found or stock is less".formatted(item.name()));

            log.info("Creating RECEIPTS record");
//            UUID storeId = storeRepository.findStoreByName(request.storeName(), item.name());
//            UUID productId = productRepository.findProductByName(request.storeName(), item.name());
            var warehouseId = warehouseRepository.findWarehouseIdByNames(request.storeName(), item.name());
            log.warn("Finding storeId `{}` and productId `{}` in warehouse", warehouseId.storeId(), warehouseId.productId());

            createProductStore(WarehouseRqDto.builder()
                    .entryType(WarehouseEntryStatus.EXPENSES)
                    .storeId(warehouseId.storeId())
                    .productId(warehouseId.productId())
                    .price(itemPrice.negate())
                    .stock(itemQuantity.negate())
                    .build());

            orderItemRsDtos.add(OrderItemRsDto.builder()
                    .name(item.name())
                    .quantity(item.quantity())
                    .price(itemPrice)
                    .build());
        }
        return orderItemRsDtos;
    }

    @Transactional
    public void warehouseRefundedProcess(OrderRsDto event) {
        log.info("Create REFUNDED record");

        event.items().forEach(
                item -> {
                    UUID storeId = storeRepository.findStoreByName(event.storeName(), item.name());
                    UUID productId = productRepository.findProductByName(event.storeName(), item.name());
                    BigDecimal quantity = BigDecimal.valueOf(item.quantity());

                    createProductStore(WarehouseRqDto.builder()
                            .entryType(WarehouseEntryStatus.REFUNDS)
                            .storeId(storeId)
                            .productId(productId)
                            .price(item.price())
                            .stock(quantity)
                            .build());
                }
        );
    }

    public WarehouseRemainderDto warehouseInventory(String storeName, String productName) {

        //todo проверка остатков, наличие сторПродукта

        return warehouseRepository.getWarehouseInventory(storeName, productName);
    }
}
