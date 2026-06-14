package ru.mrkotyaka.reviewservice.domain.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Min(1)
    @Max(5)
    @Column(name = "order_rating", nullable = false)
    private int orderRating;

    @Min(1)
    @Max(5)
    @Column(name = "courier_rating", nullable = false)
    private int courierRating;

    @Min(1)
    @Max(5)
    @Column(name = "product_rating", nullable = false)
    private int productRating;

    @Column(name = "comment", length = 300)
    @Size(min = 1, max = 300, message = "Comment must be between 1 and 300 characters")
    private String comment;
}



