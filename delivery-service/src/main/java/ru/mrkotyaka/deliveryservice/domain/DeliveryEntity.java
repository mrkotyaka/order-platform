package ru.mrkotyaka.deliveryservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mrkotyaka.commonlibs.http.order.OrderStatus;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(name = "courier_name", nullable = false)
    private String courierName;

    @Column(name = "eta_minutes", nullable = false)
    private Integer etaMinutes;
}