package ru.mrkotyaka.storeservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreProductRepository extends JpaRepository<StoreProductEntity, StoreProductId> {
    List<StoreProductEntity> findByStoreId(UUID storeId);

    List<StoreProductEntity> findByProductId(UUID productId);
}
