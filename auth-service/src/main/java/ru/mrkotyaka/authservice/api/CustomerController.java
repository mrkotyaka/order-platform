package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.authservice.domain.CustomerService;
import ru.mrkotyaka.authservice.domain.db.CustomerMapper;
import ru.mrkotyaka.commonlibs.http.auth.CustomerResponseDTO;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers(
            @RequestHeader("X-User-Roles") String authenticatedUserRole) {

        log.info("Retrieving all users from the flow");

        if (!authenticatedUserRole.equals("ROLE_ADMIN")) {
            log.warn("You are not is admin. Access denied to this info");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this info");
        }

        return customerService.getAllCustomers();
    }

    @GetMapping("/whoami")
    public CustomerResponseDTO getMe(
            @RequestHeader("X-User-Id") Long authenticatedUserId) {

        log.info("Retrieving users info");

        return customerMapper.toUserDto(customerService.getCustomerInfo(authenticatedUserId));
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerInfo(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @RequestHeader("X-User-Roles") String authenticatedUserRole) {

        log.info("Retrieving users info by id={}", id);

        var customer = customerService.getCustomerInfo(id);

        if (!customer.getId().equals(authenticatedUserId) && authenticatedUserRole.equals("ROLE_USER")) {
            log.warn("User id=`{}` tried to get info about customer id=`{}`",
                    authenticatedUserId, customer.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this info");
        }

        return customerMapper.toUserDto(customer);
    }
}
