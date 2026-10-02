package com.e.mealtracker.repository;

import com.e.mealtracker.domain.ShoppingList;
import com.e.mealtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    List<ShoppingList> findAllByUserAndStatusOrderByCreatedAtDesc(User user, String status);

    Optional<ShoppingList> findByIdAndUser(Long id, User user);

    Optional<ShoppingList> findFirstByUserAndStatus(User user, String status);
}
