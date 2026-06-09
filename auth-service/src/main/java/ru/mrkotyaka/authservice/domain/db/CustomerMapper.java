package ru.mrkotyaka.authservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.auth.AuthRqDto;
import ru.mrkotyaka.commonlibs.http.auth.CustomerRsDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    Customer toCustomerEntity(AuthRqDto request);

    CustomerRsDto toUserDto(Customer customer);
}
