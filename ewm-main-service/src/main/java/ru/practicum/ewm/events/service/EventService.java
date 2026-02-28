package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;

import java.util.List;

public interface EventService {
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequest> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}
