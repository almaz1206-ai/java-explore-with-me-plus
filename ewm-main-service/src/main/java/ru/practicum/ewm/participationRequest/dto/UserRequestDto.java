package ru.practicum.ewm.participationRequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.participationRequest.enums.StatusRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long userId;
    private Long eventId;
    private StatusRequest status;
}
