package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.StatusRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.participationRequest.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;
import ru.practicum.ewm.participationRequest.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> events = eventRepository
                .findAllByInitiatorId(userId, pageable);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate());

        Event event = eventMapper.toEntity(newEventDto, userId);

        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        Event saved = eventRepository.save(event);

        return eventMapper.toEventFullDto(saved);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository
                .findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                        new NotFoundException(String.format("Event with id: %s was not found", eventId)));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository
                .findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Event with id: %s was not found", eventId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException(
                    "Only pending or canceled events can be changed");
        }

        if (request.getEventDate() != null) {
            validateEventDate(request.getEventDate());
        }

        eventMapper.updateEventFromDto(request, event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequest> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository
                .findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                        new NotFoundException(String.format("Event with id: %s was not found", eventId)));

        return requestRepository.findAllByEventId(event.getId());
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = getOwnedEvent(userId, eventId);

        if (isAutoConfirmationDisabled(event)) {
            return emptyResult();
        }

        List<ParticipationRequest> requests =
                requestRepository.findAllByIdIn(request.getRequestIds());

        validateRequests(requests);

        if (request.getStatus() == StatusRequest.CONFIRMED) {
            return processConfirmRequests(event, requests);
        } else {
            return processRejectRequests(requests);
        }
    }

    /// Вспомогательные методы
    private Event getOwnedEvent(Long userId, Long eventId) {
        return eventRepository
                .findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                        new NotFoundException(String.format("Event with id: %s was not found", eventId)));
    }

    private boolean isAutoConfirmationDisabled(Event event) {
        return event.getParticipantLimit() == 0 || !event.getRequestModeration();
    }

    private EventRequestStatusUpdateResult emptyResult() {
        return new EventRequestStatusUpdateResult(
                List.of(),
                List.of()
        );
    }

    private void validateRequests(List<ParticipationRequest> requests) {

        for (ParticipationRequest pr : requests) {
            if (pr.getStatus() != StatusRequest.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }
        }
    }

    private EventRequestStatusUpdateResult processConfirmRequests(
            Event event,
            List<ParticipationRequest> requests) {

        int confirmedCount = event.getConfirmedRequests();
        int limit = event.getParticipantLimit();

        if (confirmedCount >= limit) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<ParticipationRequest> confirmed = new java.util.ArrayList<>();
        List<ParticipationRequest> rejected = new java.util.ArrayList<>();

        for (ParticipationRequest pr : requests) {

            if (confirmedCount >= limit) {
                throw new ConflictException("The participant limit has been reached");
            }

            pr.setStatus(StatusRequest.CONFIRMED);
            confirmed.add(pr);
            confirmedCount++;
        }

        event.setConfirmedRequests(confirmedCount);

        rejectRemainingIfLimitReached(event, rejected);

        return buildResult(confirmed, rejected);
    }

    private EventRequestStatusUpdateResult processRejectRequests(
            List<ParticipationRequest> requests) {

        List<ParticipationRequest> rejected = new java.util.ArrayList<>();

        for (ParticipationRequest pr : requests) {
            pr.setStatus(StatusRequest.REJECTED);
            rejected.add(pr);
        }

        return buildResult(List.of(), rejected);
    }

    private void rejectRemainingIfLimitReached(
            Event event,
            List<ParticipationRequest> rejected) {

        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
            return;
        }

        List<ParticipationRequest> pending =
                requestRepository.findAllByEventId(event.getId())
                        .stream()
                        .filter(r -> r.getStatus() == StatusRequest.PENDING)
                        .toList();

        for (ParticipationRequest pr : pending) {
            pr.setStatus(StatusRequest.REJECTED);
            rejected.add(pr);
        }
    }

    private EventRequestStatusUpdateResult buildResult(
            List<ParticipationRequest> confirmed,
            List<ParticipationRequest> rejected) {

        return new EventRequestStatusUpdateResult(
                confirmed.stream().map(ParticipationRequestMapper::toParticipationRequestDto).toList(),
                rejected.stream().map(ParticipationRequestMapper::toParticipationRequestDto).toList()
        );
    }

    private void validateEventDate(LocalDateTime eventDate) {

        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours in the future");
        }
    }
}