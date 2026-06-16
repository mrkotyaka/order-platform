package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductId implements Serializable {
    private UUID storeId;
    private UUID productId;
}
