package com.e.mealtracker.dto;

import com.e.mealtracker.domain.ShoppingList;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ShoppingListDto {
    private Long id;
    private String name;
    private Long planId;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private String status;
    private List<ShoppingListItemDto> items;

    public static ShoppingListDto fromEntity(ShoppingList list, List<ShoppingListItemDto> items) {
        ShoppingListDto dto = new ShoppingListDto();
        dto.setId(list.getId());
        dto.setName(list.getName());
        dto.setPlanId(list.getPlan() != null ? list.getPlan().getId() : null);
        dto.setPeriodStart(list.getPeriodStart());
        dto.setPeriodEnd(list.getPeriodEnd());
        dto.setStatus(list.getStatus());
        dto.setItems(items);
        return dto;
    }
}