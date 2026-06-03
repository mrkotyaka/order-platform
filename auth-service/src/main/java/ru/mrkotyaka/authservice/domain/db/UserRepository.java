package ru.mrkotyaka.authservice.domain.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> findByUsername(String username);
}
