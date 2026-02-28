package ru.practicum.ewm.events.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private Long categoryId;
    private Boolean paid;
    private LocalDateTime eventDate;
    private Integer confirmedRequests;
    private Long views;
}