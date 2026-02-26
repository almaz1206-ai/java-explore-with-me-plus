package ru.practicum.ewm.participationRequest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.enums.StatusRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long requester;
    private Long event;
    private StatusRequest status;
}