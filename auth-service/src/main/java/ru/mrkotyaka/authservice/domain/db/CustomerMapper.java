package ru.mrkotyaka.authservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.auth.AuthRequestDTO;
import ru.mrkotyaka.commonlibs.http.auth.CustomerResponseDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    Customer toCustomerEntity(AuthRequestDTO request);

    CustomerResponseDTO toUserDto(Customer customer);
}
