package ru.mrkotyaka.commonlibs.dto.stores;

public record StoreRqDto(
        String name,
        String address,
        int rating
) {
}
