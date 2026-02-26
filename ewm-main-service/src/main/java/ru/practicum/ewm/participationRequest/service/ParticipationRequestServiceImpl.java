package ru.practicum.ewm.participationRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.StatusRequest;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationRequest.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;
import ru.practicum.ewm.participationRequest.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    @Transactional
    public void addUserRequest(long userId, long eventId) {
        log.info("Save request");

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id: %s was not found", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id: %s was not found", eventId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    "Initiator cannot request participation in own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                    "Cannot participate in unpublished event");
        }

        boolean exists = requestRepository
                .existsByRequesterIdAndEventId(userId, eventId);

        if (exists) {
            throw new ConflictException(
                    "Participation request already exists");
        }

        StatusRequest statusRequest;

        if (!event.isRequestModeration()) {
            statusRequest = StatusRequest.CONFIRMED;
        } else {
            statusRequest = StatusRequest.PENDING;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .requester(user)
                .status(statusRequest)
                .created(LocalDateTime.now())
                .event(event)
                .build();

        participationRequestRepository.save(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long requesterId, long requestId) {
        ParticipationRequest request = participationRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request with id: %s was not found", requestId)));

        if (!request.getRequester().getId().equals(requesterId)) {
            throw new NotFoundException(String.format("Requester with id: %s was not found", requesterId));
        }

        request.setStatus(StatusRequest.CANCELLED);

        ParticipationRequest updated = participationRequestRepository.save(request);

        return ParticipationRequestMapper.toParticipationRequestDto(updated);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(long requesterId) {
        //для проверки на существующего пользователя
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException(String.format("User with id: %s was not found", requesterId)));

        return participationRequestRepository.findAllByRequesterId(requesterId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();
    }
}
