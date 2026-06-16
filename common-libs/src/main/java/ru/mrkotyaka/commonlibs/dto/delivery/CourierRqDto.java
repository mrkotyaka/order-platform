package ru.mrkotyaka.commonlibs.dto.delivery;

import java.util.UUID;

public record CourierRqDto(
        UUID userId,
        String name
) {
}
