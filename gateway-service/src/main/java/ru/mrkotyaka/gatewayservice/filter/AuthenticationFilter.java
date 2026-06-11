package ru.mrkotyaka.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final SecretKey key;

    // Конструктор принимает секрет и сразу формирует безопасный SecretKey
    public AuthenticationFilter(@Value("${jwt.secret}") String jwtSecret) {
        super(Config.class);
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public static class Config {
        // Здесь можно хранить специфичные настройки фильтра, если понадобятся
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Ищем заголовок Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("❌ Отсутствует заголовок Authorization");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("❌ Неверный формат заголовка Authorization");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Header Format");
            }

            // Вырезаем сам токен (строка после "Bearer ")
            String token = authHeader.substring(7);

            try {
                // 2. Валидируем токен с помощью нашего ключа
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                log.info("✅ Токен успешно валидирован для пользователя: {}", claims.getSubject());

                // 3. Модифицируем заголовки запроса перед отправкой в целевой микросервис
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", claims.getSubject()) // Передаем ID (Subject токена)
                        .header("X-User-Roles", claims.get("role", String.class)) // Передаем роли (если есть)
                        .build();

                // Пропускаем запрос дальше с модифицированными заголовками
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("❌ Ошибка валидации токена: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
            }
        };
    }
}