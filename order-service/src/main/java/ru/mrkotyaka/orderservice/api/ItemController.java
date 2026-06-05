package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.http.item.ItemDTO;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final OrderProcessor orderProcessor;

    @GetMapping
    public List<ItemDTO> getAllItems() {
        return orderProcessor.getAllItems();
    }
}
