package ru.mrkotyaka.orderservice.domain.db;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query(value = """
            SELECT DISTINCT o.* FROM orders o
            WHERE o.order_status = 'PENDING_PAYMENT'
            """, nativeQuery = true)
    List<OrderEntity> findAllPendingPayment();

    @EntityGraph(attributePaths = {"items"})
    Optional<OrderEntity> findWithItemsById(UUID id);

    List<OrderEntity> findAllByOrderStatus(OrderStatus orderStatus);
}
