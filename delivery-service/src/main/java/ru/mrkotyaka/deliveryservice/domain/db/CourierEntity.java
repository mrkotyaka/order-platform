package ru.mrkotyaka.deliveryservice.domain.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "couriers")
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Min(0)
    @Max(5)
    @Column(name = "rating", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "address")
    private String address;

    @Email(message = "Please provide a valid email address")
    @Column(name = "email")
    private String email;

    @Pattern(
            regexp = "^[0-9]{6,10}$",
            message = "Phone number must be 6–10 digits"
    )
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "courierId")
    private List<DeliveryEntity> deliveries = new ArrayList<>();
}
