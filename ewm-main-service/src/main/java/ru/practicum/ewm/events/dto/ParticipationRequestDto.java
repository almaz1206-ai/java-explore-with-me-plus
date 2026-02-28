package ru.practicum.ewm.events.dto;

import lombok.*;

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
    private String status; // PENDING / CONFIRMED / REJECTED / CANCELED
    private LocalDateTime created;
}