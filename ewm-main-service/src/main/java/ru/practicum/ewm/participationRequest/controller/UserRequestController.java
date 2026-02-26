package ru.practicum.ewm.participationRequest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.participationRequest.dto.CreateUserRequestDto;
import ru.practicum.ewm.participationRequest.dto.UserRequestDto;
import ru.practicum.ewm.participationRequest.service.UserRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class UserRequestController {
    private final UserRequestService requestService;

    @GetMapping
    public List<UserRequestDto> getUserRequests(@PathVariable Long requesterId) {
        return requestService.getUserRequests(requesterId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addParticipationRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @RequestBody CreateUserRequestDto createUserRequestDto
            ) {
        requestService.addUserRequest(userId, eventId, createUserRequestDto);
    }

    @PatchMapping("{requestId}/cancel")
    public UserRequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return requestService.cancelRequest(userId, requestId);
    }
}