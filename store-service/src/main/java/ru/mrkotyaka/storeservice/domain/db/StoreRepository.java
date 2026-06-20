package ru.mrkotyaka.storeservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
    boolean existsByName(String name);

    @Query(value = """
            SELECT s.id
            FROM warehouse_entry sp
            join stores s on s.id = sp.store_id
            join products p on p.id = sp.product_id
            where s.name = :store_name
            and p.name = :product_name
            """, nativeQuery = true)
    UUID findStoreByName(
            @Param("store_name") String storeName,
            @Param("product_name") String productName
    );
}
