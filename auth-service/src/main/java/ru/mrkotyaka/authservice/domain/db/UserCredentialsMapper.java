package ru.mrkotyaka.authservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.auth.AuthRequest;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.commonlibs.http.user.UserResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserCredentialsMapper {

    UserCredentials toUserEntity(AuthRequest request);

    UserResponse toUserDto(UserCredentials user);
}
