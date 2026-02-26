package ru.practicum.ewm.participationRequest.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.participationRequest.enums.StatusRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreateUserRequestDto {
    @DateTimeFormat
    @NotNull
    private LocalDateTime created;

    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    private StatusRequest status = StatusRequest.PENDING;
}