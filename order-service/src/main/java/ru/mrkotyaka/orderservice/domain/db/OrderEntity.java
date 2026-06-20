package ru.mrkotyaka.orderservice.domain.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;
import ru.mrkotyaka.orderservice.domain.OrderStatusListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
@EntityListeners(OrderStatusListener.class)
public class OrderEntity {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "address", nullable = false)
    private String deliveryAddress;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "courier_name")
    private String courierName;

    @Column(name = "eta_minutes")
    private Integer etaMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "orderId", cascade = CascadeType.PERSIST)
    private Set<OrderItemEntity> items = new LinkedHashSet<>();

    @Column(name = "comment", length = 300)
    @Size(min = 1, max = 300, message = "Comment must be between 1 and 300 characters")
    private String comment;
}
