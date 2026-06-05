package ru.mrkotyaka.orderservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    boolean existsByName(String name);

    @Query("SELECT i.price FROM ItemEntity i WHERE i.name = :name")
    Double findPriceByName(@Param("name") String name);
}
