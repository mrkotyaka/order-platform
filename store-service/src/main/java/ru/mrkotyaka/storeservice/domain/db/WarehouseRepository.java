package ru.mrkotyaka.storeservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mrkotyaka.commonlibs.dto.stores.WarehouseIdDto;
import ru.mrkotyaka.commonlibs.dto.stores.WarehouseRemainderDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseEntity, WarehouseId> {
    List<WarehouseEntity> findByStoreId(UUID storeId);

    List<WarehouseEntity> findByProductId(UUID productId);

    @Query(value = """
            SELECT sp.price
            FROM warehouse_entry sp
                     join stores s on s.id = sp.store_id
                     join products p on p.id = sp.product_id
            where s.name = :store_name
              and p.name = :product_name
              and sp.stock >= :product_quantity
            """, nativeQuery = true)
    BigDecimal findByStoreNameAndProductName(
            @Param("store_name") String storeName,
            @Param("product_name") String productName,
            @Param("product_quantity") int productQuantity
    );

    @Query(value = """
            SELECT new ru.mrkotyaka.commonlibs.dto.stores.WarehouseIdDto(s.id, p.id)
            FROM WarehouseEntity we
            join StoreEntity s on s.id = we.store.id
            join ProductEntity p on p.id = we.product.id
            where s.name = :store_name
            and p.name = :product_name
            """)
    WarehouseIdDto findWarehouseIdByNames(
            @Param("store_name") String storeName,
            @Param("product_name") String productName);


    @Query(value = """
            SELECT s.id as storeId, p.id as productId, sum(sp.stock) as remainder
            FROM warehouse_entry sp
                     join stores s on s.id = sp.store_id
                     join products p on p.id = sp.product_id
            where s.name = :store_name
              and p.name = :product_name
                        group by s.id, p.id
                        having sum(sp.stock) > 0
            """, nativeQuery = true)
    WarehouseRemainderDto getWarehouseInventory(
            @Param("store_name") String storeName,
            @Param("product_name") String productName);
}
