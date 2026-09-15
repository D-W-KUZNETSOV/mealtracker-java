package com.e.mealtracker.repository;

import com.e.mealtracker.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // ✅ базовые ингредиенты — по константе
    List<Ingredient> findAllByUsername(String username);

    Optional<Ingredient> findByNameIgnoreCaseAndUsername(String name, String username);

    Optional<Ingredient> findByIdAndUsername(Long id, String username);

    boolean existsByIdAndUsername(Long id, String username);

    // ✅ поиск по базовым
    List<Ingredient> findByNameIgnoreCaseContainingAndUsername(String query, String username);

    // ✅ все ингредиенты пользователя: базовые + его личные
    default List<Ingredient> findAllForUser(String username) {
        return findAllByUsernameIn(List.of(Ingredient.SYSTEM_USERNAME, username));
    }

    List<Ingredient> findAllByUsernameIn(List<String> usernames);

    // для delete в сервисе
    void delete(Ingredient ingredient);
}
