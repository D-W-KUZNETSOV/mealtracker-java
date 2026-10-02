package com.e.mealtracker.controller;

import com.e.mealtracker.dto.GenerateShoppingListRequest;
import com.e.mealtracker.dto.ShoppingListDto;
import com.e.mealtracker.dto.ShoppingListItemDto;
import com.e.mealtracker.service.ShoppingListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @GetMapping
    @Operation(summary = "Получить активные списки покупок")
    public ResponseEntity<List<ShoppingListDto>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shoppingListService.getAllLists(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить список по ID")
    public ResponseEntity<ShoppingListDto> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shoppingListService.getList(id, userDetails.getUsername()));
    }

    @PostMapping("/generate/{planId}")
    @Operation(summary = "Сгенерировать список покупок из плана")
    public ResponseEntity<ShoppingListDto> generate(
            @PathVariable Long planId,
            @RequestBody(required = false) GenerateShoppingListRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ShoppingListDto dto = shoppingListService.generateFromPlan(planId,
                request != null ? request.getPeriodStart() : null,
                request != null ? request.getPeriodEnd() : null,
                userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{listId}/items/{itemId}/toggle")
    @Operation(summary = "Отметить/снять галочку")
    public ResponseEntity<ShoppingListItemDto> toggle(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(shoppingListService.toggleItem(listId, itemId, userDetails.getUsername()));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Отправить список в архив")
    public ResponseEntity<Void> archive(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        shoppingListService.archiveList(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить список")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        shoppingListService.deleteList(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}