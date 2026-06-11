package ru.mrkotyaka.authservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.dto.auth.UserRsDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserRsDto toUserDto(UserEntity user);

    @Mapping(source = "id", target = "userId")
    CourierRqDto toCourierRqDto(UserEntity user);
}
