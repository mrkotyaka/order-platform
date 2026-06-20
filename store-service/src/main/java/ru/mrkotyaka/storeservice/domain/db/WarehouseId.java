package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.Embeddable;
import lombok.*;
import ru.mrkotyaka.commonlibs.enums.store.WarehouseEntryStatus;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseId implements Serializable {
    private UUID storeId;
    private UUID productId;
}
