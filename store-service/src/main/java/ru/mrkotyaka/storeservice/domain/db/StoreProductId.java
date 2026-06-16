package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@ToString
public class StoreProductId implements Serializable {
    private UUID storeId;
    private UUID productId;
}
