package ru.practicum.ewm.participationRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.participationRequest.dto.CreateUserRequestDto;
import ru.practicum.ewm.participationRequest.dto.UserRequestDto;
import ru.practicum.ewm.participationRequest.repository.UserRequestRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRequestServiceImpl implements UserRequestService {
    private final UserRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public void addUserRequest(long userId, long eventId, CreateUserRequestDto createUserRequstDto) {
        log.info("Saved request");

    }

    @Override
    public UserRequestDto cancelRequest(long requesterId, long requestId) {
        return null;
    }

    @Override
    public List<UserRequestDto> getUserRequests(long requesterId) {
        return List.of();
    }
}
