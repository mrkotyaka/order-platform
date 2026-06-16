package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.mrkotyaka.commonlibs.enums.store.ProductCategory;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private ProductCategory category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private Set<StoreProductEntity> storeProducts = new LinkedHashSet<>();
}
