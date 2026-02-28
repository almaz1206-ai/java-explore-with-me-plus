package ru.practicum.ewm.events.dto;

import lombok.*;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}

