package ru.practicum.ewm.events.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.events.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    // Получить события конкретного пользователя с пагинацией
    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    // Найти событие по id и пользователю (для проверки владения)
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    // Подсчёт опубликованных событий (пример для статистики)
    long countByInitiatorIdAndState(Long userId, EventState state);

    // Найти все опубликованные события
    Page<Event> findAllByState(EventState state, Pageable pageable);

    // Найти события по категории и состоянию
    Page<Event> findAllByCategoryIdAndState(Long categoryId,
                                            EventState state,
                                            Pageable pageable);

    // Получить события по списку id
    List<Event> findAllByIdIn(List<Long> ids);

    // Найти события по диапазону дат
    @Query("""
            select e from Event e
            where e.eventDate between :start and :end
            """)
    Page<Event> findAllByEventDateBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :eventId")
    Optional<Event> findByIdForUpdate(Long eventId);
}