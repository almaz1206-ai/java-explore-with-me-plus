package ru.practicum.ewm.events.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;

import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, int from, int size, HttpServletRequest httpRequest);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest httpRequest);
}
