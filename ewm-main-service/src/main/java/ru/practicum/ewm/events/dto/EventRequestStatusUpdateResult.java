package ru.practicum.ewm.events.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ru.practicum.ewm.participationRequest.dto.ParticipationRequestDto> confirmedRequests;
    private List<ru.practicum.ewm.participationRequest.dto.ParticipationRequestDto> rejectedRequests;
}