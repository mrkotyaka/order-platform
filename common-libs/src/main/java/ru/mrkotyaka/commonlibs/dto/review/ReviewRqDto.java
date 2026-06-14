package ru.mrkotyaka.commonlibs.dto.review;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewRqDto(
        UUID userId,
        UUID orderId,
        int orderRating,
        int courierRating,
        int productRating,
        String comment
) {
}
