package ru.practicum.ewm.participationRequest.service;

import ru.practicum.ewm.participationRequest.dto.CreateUserRequestDto;
import ru.practicum.ewm.participationRequest.dto.UserRequestDto;

import java.util.List;

public interface UserRequestService {
    void addUserRequest(long userId, long eventId, CreateUserRequestDto createUserRequstDto);

    UserRequestDto cancelRequest(long requesterId, long requestId);

    List<UserRequestDto> getUserRequests(long requesterId);
}
