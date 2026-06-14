package ru.mrkotyaka.deliveryservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourierRepository extends JpaRepository<CourierEntity, UUID> {

    @Query(value = """
            select c.*
            from couriers c
            where not exists (
                select 1
                from deliveries d
                where d.courier_id = c.id
                  and d.delivered_at is null AND canceled_at is null)
            order by c.name;
            """, nativeQuery = true)
    List<CourierEntity> findAllFree();

    @Query(value = """
            select c.*
            from couriers c
            left join (
                        select d.courier_id, count(*) qtydelivery
                        from deliveries d
                        where d.delivered_at is null AND canceled_at is null
                        group by d.courier_id) din
            on c.id = din.courier_id
            order by coalesce(din.qtydelivery,0), c.rating desc limit 1
            """, nativeQuery = true)
    Optional<CourierEntity> findOne();

    Optional<CourierEntity> findByUserId(UUID userId);

    @Query(value = """
            select c.name, coalesce(din.qtydelivery,0)
            from couriers c
            left join (
                select d.courier_id, count(*) qtydelivery
                from deliveries d
                where d.delivered_at is null AND canceled_at is null
                group by d.courier_id) din
            on c.id = din.courier_id
            order by coalesce(din.qtydelivery,0)
            """, nativeQuery = true)
    List<Object[]> findNumberDeliveriesByCourier();
}
