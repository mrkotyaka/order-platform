package ru.mrkotyaka.deliveryservice.domain.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "couriers")
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourierEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Min(0)
    @Max(5)
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @OneToMany(mappedBy = "courierId")
    private List<DeliveryEntity> deliveries = new ArrayList<>();
}
