package ru.mrkotyaka.storeservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRsDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRsDto;
import ru.mrkotyaka.storeservice.domain.db.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreProcessor {
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StoreMapper storeMapper;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<StoreRsDto> getStores() {
        log.info("Getting all saved stores");
        var stores = storeRepository.findAll();
        List<StoreRsDto> dtos = new ArrayList<>();

        for (StoreEntity store : stores) {
            dtos.add(storeMapper.toDto(store));
        }
        return dtos;
    }

    @Transactional
    public StoreRsDto createStore(StoreRqDto request) {
        var entity = storeMapper.toEntity(request);
        log.info("Saving store `{}`", entity.getName());
        return storeMapper.toDto(storeRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ProductRsDto> getProducts() {
        log.info("Getting all saved products");
        var products = productRepository.findAll();
        List<ProductRsDto> dtos = new ArrayList<>();

        for (ProductEntity product : products) {
            dtos.add(productMapper.toDto(product));
        }
        return dtos;
    }

    @Transactional
    public List<ProductRsDto> createProduct(List<ProductRqDto> request) {
        List<ProductRsDto> dtos = new ArrayList<>();

        for (var dto : request) {
            var entity = productMapper.toEntity(dto);
            productRepository.save(entity);
            dtos.add(productMapper.toDto(entity));
        }

        log.info("{} positions of products was saved successfully", dtos.size());
        return dtos;
    }
}
