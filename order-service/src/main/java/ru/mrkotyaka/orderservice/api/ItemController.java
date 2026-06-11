package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.item.ItemRqDto;
import ru.mrkotyaka.commonlibs.dto.item.ItemRsDto;
import ru.mrkotyaka.orderservice.domain.ItemProcessor;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemProcessor itemProcessor;

    @GetMapping
    public List<ItemRsDto> getAllItems(
            @RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all items");
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting all items");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all items");
        }
        return itemProcessor.getAllItems();
    }

    @PostMapping
    public List<ItemRsDto> createItems(
            @RequestBody List<ItemRqDto> request,
            @RequestHeader("X-User-Roles") String authUserRole) {
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to create new item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to create new item");
        }

        log.info("Creating a new list of items");
        return itemProcessor.createItems(request);
    }
}
