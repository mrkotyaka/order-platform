package ru.mrkotyaka.authservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.authservice.domain.CustomerService;
import ru.mrkotyaka.commonlibs.http.auth.CustomerResponseDTO;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponseDTO> getAllCustomers() {
        log.info("Retrieving all users from the flow");
        return customerService.getAllCustomers();
    }
}
