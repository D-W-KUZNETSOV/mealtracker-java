package com.e.mealtracker.service;

import com.e.mealtracker.dto.BodyMeasurementDto;
import com.e.mealtracker.dto.CreateBodyMeasurementRequest;
import com.e.mealtracker.entity.BodyMeasurement;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.BodyMeasurementRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты BodyMeasurementService")
class BodyMeasurementServiceTest {

    @Mock
    private BodyMeasurementRepository repository;

    @InjectMocks
    private BodyMeasurementService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("dmitriy");
    }

    // ============================================================
    // listByUser
    // ============================================================

    @Test
    @DisplayName("listByUser: возвращает список замеров пользователя")
    void shouldReturnUserMeasurements() {
        BodyMeasurement m1 = measurement(1L, user, LocalDate.of(2026, 9, 26),
                new BigDecimal("81.50"));
        BodyMeasurement m2 = measurement(2L, user, LocalDate.of(2026, 9, 20),
                new BigDecimal("82.00"));

        when(repository.findByUserOrderByMeasuredAtDesc(user))
                .thenReturn(List.of(m1, m2));

        List<BodyMeasurementDto> result = service.listByUser(user);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getWeightKg())
                .isEqualByComparingTo("81.50");
        assertThat(result.get(1).getWeightKg())
                .isEqualByComparingTo("82.00");
        verify(repository).findByUserOrderByMeasuredAtDesc(user);
    }

    @Test
    @DisplayName("listByUser: пустой список — возвращает пустой список")
    void shouldReturnEmptyList() {
        when(repository.findByUserOrderByMeasuredAtDesc(user))
                .thenReturn(List.of());

        List<BodyMeasurementDto> result = service.listByUser(user);

        assertThat(result).isEmpty();
    }

    // ============================================================
    // create
    // ============================================================

    @Test
    @DisplayName("create: сохраняет замер со всеми полями")
    void shouldCreateMeasurementWithAllFields() {
        CreateBodyMeasurementRequest req = new CreateBodyMeasurementRequest();
        req.setMeasuredAt(LocalDate.of(2026, 9, 26));
        req.setWeightKg(new BigDecimal("81.50"));
        req.setChestCm(new BigDecimal("100.00"));
        req.setWaistCm(new BigDecimal("92.00"));
        req.setBellyCm(new BigDecimal("95.00"));
        req.setHipsCm(new BigDecimal("98.00"));
        req.setThighCm(new BigDecimal("58.00"));
        req.setArmCm(new BigDecimal("35.00"));
        req.setNeckCm(new BigDecimal("40.00"));
        req.setNote("Первый замер");

        when(repository.save(any(BodyMeasurement.class)))
                .thenAnswer(inv -> {
                    BodyMeasurement m = inv.getArgument(0);
                    m.setId(10L);
                    return m;
                });

        BodyMeasurementDto result = service.create(user, req);

        ArgumentCaptor<BodyMeasurement> captor =
                ArgumentCaptor.forClass(BodyMeasurement.class);
        verify(repository).save(captor.capture());
        BodyMeasurement saved = captor.getValue();

        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getMeasuredAt()).isEqualTo(LocalDate.of(2026, 9, 26));
        assertThat(saved.getWeightKg()).isEqualByComparingTo("81.50");
        assertThat(saved.getBellyCm()).isEqualByComparingTo("95.00");
        assertThat(saved.getNote()).isEqualTo("Первый замер");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getWeightKg()).isEqualByComparingTo("81.50");
    }

    @Test
    @DisplayName("create: минимальный замер (только дата и вес)")
    void shouldCreateMinimalMeasurement() {
        CreateBodyMeasurementRequest req = new CreateBodyMeasurementRequest();
        req.setMeasuredAt(LocalDate.of(2026, 9, 26));
        req.setWeightKg(new BigDecimal("80.00"));

        when(repository.save(any(BodyMeasurement.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BodyMeasurementDto result = service.create(user, req);

        assertThat(result.getWeightKg()).isEqualByComparingTo("80.00");
        assertThat(result.getBellyCm()).isNull();
        assertThat(result.getNote()).isNull();
    }

    // ============================================================
    // delete
    // ============================================================

    @Test
    @DisplayName("delete: удаляет замер пользователя")
    void shouldDeleteOwnMeasurement() {
        BodyMeasurement m = measurement(5L, user, LocalDate.now(),
                new BigDecimal("81.00"));

        when(repository.findByIdAndUser(5L, user)).thenReturn(Optional.of(m));

        service.delete(user, 5L);

        verify(repository).findByIdAndUser(5L, user);
        verify(repository).delete(m);
    }

    @Test
    @DisplayName("delete: бросает ResourceNotFoundException, если замер не найден")
    void shouldThrowWhenMeasurementNotFound() {
        when(repository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(user, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(repository, never()).delete(any());
    }

    // ============================================================
    // helper
    // ============================================================

    private BodyMeasurement measurement(Long id, User user, LocalDate date,
                                        BigDecimal weight) {
        BodyMeasurement m = new BodyMeasurement();
        m.setId(id);
        m.setUser(user);
        m.setMeasuredAt(date);
        m.setWeightKg(weight);
        return m;
    }
}