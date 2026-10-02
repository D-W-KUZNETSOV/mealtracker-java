package com.e.mealtracker.dto;

import com.e.mealtracker.domain.ShoppingListItem;
import lombok.Data;

@Data
public class ShoppingListItemDto {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String category;
    private Double quantityGrams;
    private String unitType;
    private Boolean isChecked;

    public static ShoppingListItemDto fromEntity(ShoppingListItem item) {
        ShoppingListItemDto dto = new ShoppingListItemDto();
        dto.setId(item.getId());
        if (item.getIngredient() != null) {
            dto.setIngredientId(item.getIngredient().getId());
        }
        dto.setIngredientName(item.getIngredientName());
        dto.setCategory(item.getCategory());
        dto.setQuantityGrams(item.getQuantityGrams());
        dto.setUnitType(item.getUnitType() != null ? item.getUnitType().name() : null);
        dto.setIsChecked(item.getIsChecked());
        return dto;
    }
}