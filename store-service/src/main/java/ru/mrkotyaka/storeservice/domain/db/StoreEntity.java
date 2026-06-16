package ru.mrkotyaka.storeservice.domain.db;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "stores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Range(min = 1, max = 5, message = "Rating must be between 1 and 5 stars")
    private int rating;

    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private Set<StoreProductEntity> inventory = new LinkedHashSet<>();

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closingTime = now.withHour(20).withMinute(0).withSecond(0).withNano(0);
        return now.isBefore(closingTime);
    }
}
