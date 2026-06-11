package ru.mrkotyaka.commonlibs.dto.courier;

import java.util.UUID;

public record CourierRqDto(
        UUID userId,
        String name
) {
}
