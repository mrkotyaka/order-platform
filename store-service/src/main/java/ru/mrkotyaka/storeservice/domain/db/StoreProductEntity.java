package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "store_products",
        indexes = {
                @Index(name = "idx_store_product_store_id", columnList = "store_id"),
                @Index(name = "idx_store_product_product_id", columnList = "product_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "product_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StoreProductEntity {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private StoreProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storeId")
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(nullable = false)
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "Stock must be positive")
    private BigDecimal stock;
}
