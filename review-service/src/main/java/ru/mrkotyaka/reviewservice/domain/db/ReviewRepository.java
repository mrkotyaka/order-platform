package ru.mrkotyaka.reviewservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    ReviewEntity findByOrderId(UUID orderId);
}
