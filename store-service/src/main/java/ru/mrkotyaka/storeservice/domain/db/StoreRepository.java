package ru.mrkotyaka.storeservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<StoreEntity, UUID> {
}
