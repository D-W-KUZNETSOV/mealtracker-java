package com.e.mealtracker.service;

import com.e.mealtracker.dto.ProductLookupResponse;
import com.e.mealtracker.exception.OffUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Service
public class OpenFoodFactsClient {

    private static final String BASE_URL = "https://world.openfoodfacts.org";
    // ⚠️ OFF требует User-Agent с контактом. Поменяй email на свой!
    private static final String USER_AGENT = "MealTracker/1.0 (Dmitriy; your-email@example.com)";

    private final RestClient restClient;

    public OpenFoodFactsClient(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 сек
        factory.setReadTimeout(5000);
        this.restClient = builder
                .baseUrl(BASE_URL)
                .defaultHeader("User-Agent", USER_AGENT)
                .requestFactory(factory)
                .build();
    }

    @SuppressWarnings("unchecked")
    public ProductLookupResponse lookupByBarcode(String code) {
        log.info("OFF lookup for barcode {}", code);

        Map<String, Object> raw;
        try {
            raw = restClient.get()
                    .uri("/api/v2/product/{code}.json", code)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientException e) {
            log.error("OFF request failed for barcode {}", code, e);
            throw new OffUnavailableException("Open Food Facts недоступен", e);
        }

        // OFF отдаёт 200 даже на "не найдено", но со status=0
        Integer status = raw != null ? (Integer) raw.get("status") : null;
        if (status == null || status != 1) {
            log.info("Barcode {} not found in OFF", code);
            return ProductLookupResponse.builder()
                    .barcode(code)
                    .found(false)
                    .build();
        }

        Map<String, Object> product = (Map<String, Object>) raw.get("product");
        Map<String, Object> nutriments = product != null
                ? (Map<String, Object>) product.get("nutriments")
                : null;

        return ProductLookupResponse.builder()
                .barcode(code)
                .found(true)
                .name(product != null ? (String) product.getOrDefault("product_name", "") : "")
                .caloriesPer100g(getDouble(nutriments, "energy-kcal_100g"))
                .proteinsPer100g(getDouble(nutriments, "proteins_100g"))
                .fatsPer100g(getDouble(nutriments, "fat_100g"))
                .carbsPer100g(getDouble(nutriments, "carbohydrates_100g"))
                .id(null)
                .build();
    }

    private Double getDouble(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object v = map.get(key);
        return (v instanceof Number n) ? n.doubleValue() : null;
    }
}
