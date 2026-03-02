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

        @Query("""
                SELECT c FROM CourierEntity c
                           LEFT JOIN c.deliveries d
                                      WHERE function('timestamp_add_minutes', d.deliveryDateTime, d.etaMinutes) < :now
                                                      ORDER BY c.rating DESC
                """)
        Optional<List<CourierEntity>> findAllFree(@Param("now") LocalDateTime now);

        @Query("""
                SELECT c FROM CourierEntity c
                           LEFT JOIN c.deliveries d
                                      WHERE function('timestamp_add_minutes', d.deliveryDateTime, d.etaMinutes) < :now
                                                      ORDER BY c.rating DESC LIMIT 1
                """)
        Optional<CourierEntity> findOneFree(LocalDateTime now);
}
