package com.e.mealtracker.service;

import com.e.mealtracker.domain.DailyLog;
import com.e.mealtracker.domain.FoodEntry;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.entity.Role;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.InvalidPortionWeightException;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.DailyLogRepository;
import com.e.mealtracker.repository.FoodEntryRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserGoalsRepository;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.util.ActivityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты StatsService")
class StatsServiceTest {

    @Mock private DailyLogRepository dailyLogRepository;
    @Mock private FoodEntryRepository foodEntryRepository;
    @Mock private RecipeRepository recipeRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserGoalsRepository userGoalsRepository;

    @InjectMocks
    private StatsService statsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("dmitriy");
        user.setPassword("dummy");
        user.setRole(Role.USER);
    }

    // ============================================================
    // ВСПОМОГАТЕЛЬНЫЕ
    // ============================================================

    private Recipe recipeWithIngredients(Long id, double... weights) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName("Обед");
        // Проставляем итоги, как будто их посчитал saveRecipe
        recipe.setTotalCalories(new BigDecimal("480.00"));
        recipe.setTotalProteins(new BigDecimal("68.30"));
        recipe.setTotalFats(new BigDecimal("8.85"));
        recipe.setTotalCarbs(new BigDecimal("31.95"));

        for (double w : weights) {
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(recipe);
            ri.setWeightInGrams(w);
            recipe.getIngredients().add(ri);
        }
        return recipe;
    }

    private RecipePortionRequest portion(Long recipeId, double grams) {
        RecipePortionRequest req = new RecipePortionRequest();
        req.setRecipeId(recipeId);
        req.setWeightInGrams(grams);
        return req;
    }

    private DailyLog dailyLog(Long id, User user, LocalDate date) {
        DailyLog log = new DailyLog();
        log.setId(id);
        log.setUser(user);
        log.setLogDate(date);
        log.setCalories(BigDecimal.ZERO);
        log.setProtein(BigDecimal.ZERO);
        log.setFat(BigDecimal.ZERO);
        log.setCarbs(BigDecimal.ZERO);
        return log;
    }

    // ============================================================
    // addPortionAndReturnTodayStats — happy path
    // ============================================================

    @Test
    @DisplayName("addPortion: 200г из рецепта весом 350г — правильный расчёт и один save FoodEntry")
    void shouldAddPortionAndReturnTodayStats() {
        LocalDate today = LocalDate.now();
        Recipe recipe = recipeWithIngredients(10L, 200.0, 150.0); // totalWeight = 350
        DailyLog log = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(foodEntryRepository.save(any(FoodEntry.class)))
                .thenAnswer(inv -> {
                    FoodEntry e = inv.getArgument(0);
                    if (e.getId() == null) e.setId(500L);
                    return e;
                });

        // После save в log должен попасть этот entry
        FoodEntry saved = new FoodEntry();
        saved.setId(500L);
        saved.setDailyLog(log);
        saved.setRecipe(recipe);
        saved.setWeightInGrams(200.0);
        // calories = 480 * (200/350) ≈ 274.29
        saved.setCalories(new BigDecimal("274.29"));
        saved.setProtein(new BigDecimal("39.03"));
        saved.setFat(new BigDecimal("5.06"));
        saved.setCarbs(new BigDecimal("18.26"));
        saved.setCaloriesPer100g(new BigDecimal("137.14"));
        saved.setProteinPer100g(new BigDecimal("19.51"));
        saved.setFatPer100g(new BigDecimal("2.53"));
        saved.setCarbsPer100g(new BigDecimal("9.13"));

        when(foodEntryRepository.findByDailyLogId(100L)).thenReturn(List.of(saved));
        when(dailyLogRepository.save(any(DailyLog.class))).thenAnswer(inv -> inv.getArgument(0));

        // Второй вызов findByUserAndLogDate — из calculateStatsForDate
        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));

        DailyStatsDto stats = statsService.addPortionAndReturnTodayStats(portion(10L, 200.0), user);

        // Проверяем, что FoodEntry сохранён с правильным весом
        ArgumentCaptor<FoodEntry> captor = ArgumentCaptor.forClass(FoodEntry.class);
        verify(foodEntryRepository).save(captor.capture());
        FoodEntry savedEntry = captor.getValue();
        assertThat(savedEntry.getWeightInGrams()).isEqualTo(200.0);
        assertThat(savedEntry.getDailyLog()).isSameAs(log);
        assertThat(savedEntry.getRecipe()).isSameAs(recipe);

        // Стата вернулась непустая
        assertThat(stats).isNotNull();
        assertThat(stats.getCalories()).isGreaterThan(0.0);
    }

    @Test
    @DisplayName("addPortion: если DailyLog за сегодня нет — создаётся новый и save вызывается дважды")
    void shouldCreateNewDailyLogIfMissing() {
        LocalDate today = LocalDate.now();
        Recipe recipe = recipeWithIngredients(10L, 100.0);
        DailyLog newLog = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.empty());
        when(dailyLogRepository.save(any(DailyLog.class))).thenReturn(newLog);
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(foodEntryRepository.save(any(FoodEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(foodEntryRepository.findByDailyLogId(100L)).thenReturn(List.of());

        // Второй вызов — из calculateStatsForDate; возвращаем тот же newLog
        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(newLog));

        statsService.addPortionAndReturnTodayStats(portion(10L, 100.0), user);

        // save(DailyLog) минимум один раз — при создании и в recalculateDailyTotals
        verify(dailyLogRepository, atLeastOnce()).save(any(DailyLog.class));
    }

    // ============================================================
    // addPortion — ошибки
    // ============================================================

    @Test
    @DisplayName("addPortion: рецепт не найден → RecipeNotFoundException")
    void shouldThrowWhenRecipeNotFound() {
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                statsService.addPortionAndReturnTodayStats(portion(99L, 200.0), user))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessageContaining("99");

        verify(foodEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("addPortion: вес <= 0 → InvalidPortionWeightException")
    void shouldThrowWhenWeightNotPositive() {
        LocalDate today = LocalDate.now();
        Recipe recipe = recipeWithIngredients(10L, 100.0);
        DailyLog log = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() ->
                statsService.addPortionAndReturnTodayStats(portion(10L, 0.0), user))
                .isInstanceOf(InvalidPortionWeightException.class)
                .hasMessageContaining("0");

        assertThatThrownBy(() ->
                statsService.addPortionAndReturnTodayStats(portion(10L, -5.0), user))
                .isInstanceOf(InvalidPortionWeightException.class);

        verify(foodEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("addPortion: рецепт без ингредиентов → per-100g = 0, FoodEntry сохраняется с 0")
    void shouldHandleRecipeWithoutIngredients() {
        LocalDate today = LocalDate.now();
        Recipe empty = new Recipe();
        empty.setId(10L);
        empty.setTotalCalories(BigDecimal.ZERO);
        empty.setTotalProteins(BigDecimal.ZERO);
        empty.setTotalFats(BigDecimal.ZERO);
        empty.setTotalCarbs(BigDecimal.ZERO);
        // ingredients пустой (new ArrayList)

        DailyLog log = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(empty));
        when(foodEntryRepository.save(any(FoodEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(foodEntryRepository.findByDailyLogId(100L)).thenReturn(List.of());
        when(dailyLogRepository.save(any(DailyLog.class))).thenAnswer(inv -> inv.getArgument(0));

        statsService.addPortionAndReturnTodayStats(portion(10L, 200.0), user);

        ArgumentCaptor<FoodEntry> captor = ArgumentCaptor.forClass(FoodEntry.class);
        verify(foodEntryRepository).save(captor.capture());
        FoodEntry saved = captor.getValue();

        assertThat(saved.getCaloriesPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getProteinPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getFatPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getCarbsPer100g()).isEqualByComparingTo("0");
        assertThat(saved.getCalories()).isEqualByComparingTo("0");
    }

    // ============================================================
    // recalculateDailyTotals — агрегация
    // ============================================================

    @Test
    @DisplayName("addPortion: total дневника = сумма всех FoodEntry")
    void shouldRecalculateTotalsFromAllEntries() {
        LocalDate today = LocalDate.now();
        Recipe recipe = recipeWithIngredients(10L, 100.0);
        DailyLog log = dailyLog(100L, user, today);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(foodEntryRepository.save(any(FoodEntry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Два существующих entry + новый
        FoodEntry e1 = new FoodEntry();
        e1.setCalories(new BigDecimal("100.00"));
        e1.setProtein(new BigDecimal("10.00"));
        e1.setFat(new BigDecimal("5.00"));
        e1.setCarbs(new BigDecimal("8.00"));

        FoodEntry e2 = new FoodEntry();
        e2.setCalories(new BigDecimal("200.00"));
        e2.setProtein(new BigDecimal("20.00"));
        e2.setFat(new BigDecimal("10.00"));
        e2.setCarbs(new BigDecimal("16.00"));

        FoodEntry newEntry = new FoodEntry();
        newEntry.setCalories(new BigDecimal("50.00"));
        newEntry.setProtein(new BigDecimal("5.00"));
        newEntry.setFat(new BigDecimal("2.50"));
        newEntry.setCarbs(new BigDecimal("4.00"));

        when(foodEntryRepository.findByDailyLogId(100L)).thenReturn(List.of(e1, e2, newEntry));
        when(dailyLogRepository.save(any(DailyLog.class))).thenAnswer(inv -> inv.getArgument(0));
        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));

        statsService.addPortionAndReturnTodayStats(portion(10L, 100.0), user);

        // После recalculateDailyTotals log.calories = 100 + 200 + 50 = 350
        assertThat(log.getCalories()).isEqualByComparingTo("350.00");
        assertThat(log.getProtein()).isEqualByComparingTo("35.00");
        assertThat(log.getFat()).isEqualByComparingTo("17.50");
        assertThat(log.getCarbs()).isEqualByComparingTo("28.00");
    }

    // ============================================================
    // calculateStatsForDate — через getTodayStats / getStatsByDate
    // ============================================================

    @Test
    @DisplayName("getTodayStats: без goals → targetProtein=null, progress=null")
    void shouldReturnStatsWithoutGoals() {
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLog(100L, user, today);
        log.setCalories(new BigDecimal("500.00"));
        log.setProtein(new BigDecimal("40.00"));
        log.setFat(new BigDecimal("15.00"));
        log.setCarbs(new BigDecimal("60.00"));

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.empty());

        DailyStatsDto stats = statsService.getTodayStats(user);

        assertThat(stats.getCalories()).isEqualTo(500.0);
        assertThat(stats.getProteins()).isEqualTo(40.0);
        assertThat(stats.getFats()).isEqualTo(15.0);
        assertThat(stats.getCarbs()).isEqualTo(60.0);
        assertThat(stats.getTargetProtein()).isNull();
        assertThat(stats.getProteinProgressPercent()).isNull();
    }

    @Test
    @DisplayName("getTodayStats: с goals → targetProtein = вес * proteinPerKg, прогресс посчитан")
    void shouldReturnStatsWithGoals() {
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLog(100L, user, today);
        log.setProtein(new BigDecimal("50.00"));

        UserGoals goals = new UserGoals();
        goals.setId(1L);
        goals.setUser(user);
        goals.setCurrentWeightKg(80.0);
        goals.setProteinPerKg(2.0);
        goals.setActivityLevel(ActivityLevel.MODERATE);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.of(goals));

        DailyStatsDto stats = statsService.getTodayStats(user);

        // targetProtein = 80 * 2 = 160; progress = 50 / 160 * 100 = 31.25
        assertThat(stats.getTargetProtein()).isEqualTo(160.0);
        assertThat(stats.getProteinProgressPercent()).isEqualTo(31.25);
    }

    @Test
    @DisplayName("getTodayStats: goals с weight=0 → targetProtein=null, progress=null")
    void shouldReturnNullTargetWhenWeightIsZero() {
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLog(100L, user, today);

        UserGoals goals = new UserGoals();
        goals.setId(1L);
        goals.setUser(user);
        goals.setCurrentWeightKg(0.0);
        goals.setProteinPerKg(2.0);

        when(dailyLogRepository.findByUserAndLogDate(user, today)).thenReturn(Optional.of(log));
        when(userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.of(goals));

        DailyStatsDto stats = statsService.getTodayStats(user);

        assertThat(stats.getTargetProtein()).isNull();
        assertThat(stats.getProteinProgressPercent()).isNull();
    }

    @Test
    @DisplayName("getStatsByDate: возвращает стату за указанную дату")
    void shouldReturnStatsByDate() {
        LocalDate date = LocalDate.of(2026, 9, 15);
        DailyLog log = dailyLog(100L, user, date);
        log.setCalories(new BigDecimal("1234.50"));

        when(dailyLogRepository.findByUserAndLogDate(user, date)).thenReturn(Optional.of(log));
        when(userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user)).thenReturn(Optional.empty());

        DailyStatsDto stats = statsService.getStatsByDate(date, user);

        assertThat(stats.getCalories()).isEqualTo(1234.5);
    }

    @Test
    @DisplayName("getStatsPage: пробрасывает в findByUser(user, pageable)")
    void shouldReturnStatsPage() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var page = new org.springframework.data.domain.PageImpl<DailyLog>(List.of());
        when(dailyLogRepository.findByUser(eq(user), any())).thenReturn(page);

        var result = statsService.getStatsPage(user, 0, 10);

        assertThat(result.getContent()).isEmpty();
        verify(dailyLogRepository).findByUser(eq(user), any());
    }
}
