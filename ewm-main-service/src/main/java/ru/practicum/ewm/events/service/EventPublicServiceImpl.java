package ru.practicum.ewm.events.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.EventState;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.repository.EventSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, int from, int size, HttpServletRequest httpRequest) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime start = rangeStart != null
                ? LocalDateTime.parse(rangeStart, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;
        LocalDateTime end = rangeEnd != null
                ? LocalDateTime.parse(rangeEnd, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null;

        if (start != null && end != null && end.isBefore(start)) {
            throw new BadRequestException("rangeEnd must not be before rangeStart");
        }

        if (start == null) start = LocalDateTime.now();

        Specification<Event> spec = Specification
                .where(EventSpecification.hasState(EventState.PUBLISHED))
                .and(EventSpecification.hasText(text))
                .and(EventSpecification.hasCategories(categories))
                .and(EventSpecification.hasPaid(paid))
                .and(EventSpecification.eventDateAfter(start))
                .and(EventSpecification.eventDateBefore(end))
                .and(EventSpecification.isAvailable(onlyAvailable));

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        saveHit(httpRequest);

        Map<Long, Long> viewsMap = getViewsMap(events);

        return events.stream()
                .map(e -> {
                    EventShortDto dto = EventMapper.toEventShortDto(e);
                    dto.setViews(viewsMap.getOrDefault(e.getId(), 0L));
                    return dto;
                })
                .toList();
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest httpRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        saveHit(httpRequest);

        long views = 0L;
        try {
            List<ViewStatsDto> stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(10),
                    LocalDateTime.now().plusYears(10),
                    List.of("/events/" + eventId),
                    true
            );
            views = stats.isEmpty() ? 0L : stats.get(0).getHits();
        } catch (Exception ignored) {
            // stats service unavailable - not critical
        }

        EventFullDto dto = EventMapper.toEventFullDto(event);
        dto.setViews(views);
        return dto;
    }

    private void saveHit(HttpServletRequest request) {
        try {
            statsClient.saveHit(new EndpointHitDto(
                    null,
                    "ewm-main-service",
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        } catch (Exception ignored) {
            // stats service unavailable - not critical
        }
    }

    private Map<Long, Long> getViewsMap(List<Event> events) {
        if (events.isEmpty()) return Map.of();
        try {
            List<String> uris = events.stream()
                    .map(e -> "/events/" + e.getId())
                    .toList();
            List<ViewStatsDto> stats = statsClient.getStats(
                    LocalDateTime.now().minusYears(10),
                    LocalDateTime.now().plusYears(10),
                    uris,
                    true
            );
            return stats.stream()
                    .collect(Collectors.toMap(
                            s -> Long.parseLong(s.getUri().replace("/events/", "")),
                            ViewStatsDto::getHits
                    ));
        } catch (Exception e) {
            return Map.of();
        }
    }
}

