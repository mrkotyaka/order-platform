package ru.mrkotyaka.authservice.domain.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.mrkotyaka.commonlibs.http.auth.UserRoles;

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

    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be 10 digits"
    )
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRoles roles;
}
