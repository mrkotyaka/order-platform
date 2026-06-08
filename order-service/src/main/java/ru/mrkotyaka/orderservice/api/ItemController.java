package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.http.item.ItemDTO;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final OrderProcessor orderProcessor;

    @GetMapping
    public List<ItemDTO> getAllItems(
            @RequestHeader("X-User-Roles") String authenticatedUserRole) {
        log.info("Retrieving all items from the flow");
        if(authenticatedUserRole.equals("ROLE_USER")){
            log.warn("You are not is admin. Access denied to getting all items");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all items");
        }
        return orderProcessor.getAllItems();
    }
}
