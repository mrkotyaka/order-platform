package ru.mrkotyaka.deliveryservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, Long> {

    @Query(value = """
            SELECT c.* FROM couriers c
            LEFT JOIN deliveries d ON c.id = d.courier_id
            WHERE d.id IS NULL
            OR (d.delivery_datetime + (d.eta_minutes || ' minutes')::interval) < :now
            ORDER BY c.rating DESC
            """, nativeQuery = true)
    Optional<List<CourierEntity>> findAllFree(@Param("now") LocalDateTime now);

    @Query(value = """
            SELECT c.* FROM couriers c
            LEFT JOIN deliveries d ON c.id = d.courier_id
            WHERE d.id IS NULL
            OR (d.delivery_datetime + (d.eta_minutes || ' minutes')::interval) < :now
            ORDER BY c.rating DESC LIMIT 1
            """, nativeQuery = true)
    Optional<CourierEntity> findOneFree(LocalDateTime now);
}
