package ru.mrkotyaka.storeservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRsDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final StoreProcessor storeProcessor;

    @GetMapping
    public ResponseEntity<List<ProductRsDto>> getProducts() {
        log.info("REST: Getting all products");
        var result = storeProcessor.getProducts();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<List<ProductRsDto>> createProduct(@RequestBody List<ProductRqDto> request) {
        log.info("Start to save list of products `{}`", request.size());
        var result = storeProcessor.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
