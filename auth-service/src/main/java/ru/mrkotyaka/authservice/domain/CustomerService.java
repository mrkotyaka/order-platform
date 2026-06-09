package ru.mrkotyaka.authservice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.authservice.domain.db.Customer;
import ru.mrkotyaka.authservice.domain.db.CustomerMapper;
import ru.mrkotyaka.authservice.domain.db.CustomerRepository;
import ru.mrkotyaka.commonlibs.http.auth.AuthRqDto;
import ru.mrkotyaka.commonlibs.http.auth.CustomerRsDto;
import ru.mrkotyaka.commonlibs.http.auth.CustomerRoles;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomerMapper customerMapper;

    public String register(AuthRqDto request) {
        Customer customer = Customer.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .address(request.address())
                .email(request.email())
                .phone(request.phone())
                .roles(CustomerRoles.ROLE_USER)
                .notificationPreference(request.notificationPreference())
                .build();

        customerRepository.save(customer);
        return "Customer registered successfully";
    }

    public String login(AuthRqDto request) {
        Customer customer = customerRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(customer);
    }

    public List<CustomerRsDto> getAllCustomers() {
        List<CustomerRsDto> allCustomersDTO = new ArrayList<>();
        var allCustomers = customerRepository.findAll();
        for (var customer : allCustomers) {
            allCustomersDTO.add(customerMapper.toUserDto(customer));
        }
        return allCustomersDTO;
    }

    public Customer getCustomerInfo(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
