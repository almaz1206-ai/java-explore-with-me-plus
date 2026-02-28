package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonProperty("category")
    private Long categoryId;

    private Location location;

    private Boolean paid;

    private LocalDateTime eventDate;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction; // PUBLISH_EVENT / REJECT_EVENT
}
