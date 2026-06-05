package ru.mrkotyaka.deliveryservice.domain.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deliveries")
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private CourierEntity courierId;

    @Column(name = "eta_minutes", nullable = false)
    private Integer etaMinutes;

    @Column(name = "delivery_datetime")
    private LocalDateTime deliveryDateTime;
}