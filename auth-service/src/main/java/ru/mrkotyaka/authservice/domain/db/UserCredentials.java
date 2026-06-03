package ru.mrkotyaka.authservice.domain.db;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_credentials")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String roles;
}
