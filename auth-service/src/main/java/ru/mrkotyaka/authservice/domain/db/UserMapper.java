package ru.mrkotyaka.authservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.auth.UserRsDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    UserRsDto toUserDto(UserEntity user);

    @Mapping(source = "id", target = "userId")
    CourierRqDto toCourierRqDto(UserEntity user);
}