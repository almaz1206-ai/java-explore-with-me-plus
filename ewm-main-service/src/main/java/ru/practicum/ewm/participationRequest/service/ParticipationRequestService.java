package ru.practicum.ewm.participationRequest.service;

import ru.practicum.ewm.participationRequest.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    void addUserRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long requesterId, long requestId);

    List<ParticipationRequestDto> getUserRequests(long requesterId);
}
