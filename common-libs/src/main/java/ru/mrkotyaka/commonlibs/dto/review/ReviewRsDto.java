package ru.mrkotyaka.commonlibs.dto.review;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReviewRsDto(
        UUID reviewId,
        UUID orderId,
        UUID userId,
        int orderRating,
        int courierRating,
        int productRating,
        String comment
) {
}
