package ru.practicum.ewm.events.dto;

import lombok.*;
import ru.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private Long categoryId;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private LocalDateTime createdOn;
    private LocalDateTime eventDate;
    private LocalDateTime publishedOn;
    private EventState state; // PENDING / PUBLISHED / CANCELED
    private Integer confirmedRequests;
    private Long views;
    private Long initiatorId;
}