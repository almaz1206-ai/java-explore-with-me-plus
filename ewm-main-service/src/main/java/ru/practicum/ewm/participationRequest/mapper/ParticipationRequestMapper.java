package ru.practicum.ewm.participationRequest.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;


@UtilityClass
public class ParticipationRequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        if (request == null) {
            return null;
        } else {
            ParticipationRequestDto.ParticipationRequestDtoBuilder requestDto = ParticipationRequestDto.builder();
            requestDto.requester(request.getRequester().getId());
            requestDto.event(request.getEvent().getId());
            requestDto.id(request.getId());
            requestDto.status(request.getStatus());
            requestDto.created(request.getCreated());
            return requestDto.build();
        }
    }
}
