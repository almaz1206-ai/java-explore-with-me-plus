package ru.practicum.ewm.participationRequest.dto;

import jakarta.validation.constraints.NotBlank;
import ru.practicum.ewm.participationRequest.enums.StatusRequest;

public class UpdateUserRequestDto {
    @NotBlank
    private StatusRequest status;
}
