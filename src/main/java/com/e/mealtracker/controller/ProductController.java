package com.e.mealtracker.controller;

import com.e.mealtracker.dto.ProductLookupResponse;
import com.e.mealtracker.service.OpenFoodFactsClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/api/products")
public class ProductController {

    private final OpenFoodFactsClient offClient;

    @GetMapping("/barcode/{code}")
    @Operation(summary = "Найти продукт по штрихкоду в Open Food Facts")
    public ProductLookupResponse lookupByBarcode(@PathVariable String code) {
        return offClient.lookupByBarcode(code);
    }
}
