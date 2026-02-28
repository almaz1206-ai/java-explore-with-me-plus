package ru.practicum.ewm.participationRequest.dto;

import lombok.*;
import ru.practicum.ewm.enums.StatusRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    private Long requesterId;
    private Long eventId;
    private StatusRequest status; // PENDING / CONFIRMED / REJECTED / CANCELED
    private LocalDateTime created;
}