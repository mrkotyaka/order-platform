package ru.mrkotyaka.deliveryservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
    Optional<DeliveryEntity> findByOrderId(Long orderId);
}
