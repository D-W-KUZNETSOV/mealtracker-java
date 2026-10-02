package com.e.mealtracker.repository;

import com.e.mealtracker.domain.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    List<ShoppingListItem> findAllByListId(Long listId);
}
