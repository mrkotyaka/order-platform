//package ru.mrkotyaka.storeservice.domain.db;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.UuidGenerator;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(name = "revenue")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class RevenueEntity {
//
//    @Id
//    @UuidGenerator
//    private UUID id;
//
//    @Column(name = "store_id")
//    private UUID storeId;
//
//    @Column(name = "order_id")
//    private UUID orderId;
//
//    private BigDecimal amount;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt = LocalDateTime.now();
//}
