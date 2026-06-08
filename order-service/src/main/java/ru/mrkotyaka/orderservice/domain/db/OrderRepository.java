package ru.mrkotyaka.orderservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query(value = """
            SELECT DISTINCT o.* FROM orders o
            WHERE o.order_status = 'PENDING_PAYMENT'
            """, nativeQuery = true)
    List<OrderEntity> findAllPendingPayment();
}
