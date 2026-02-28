package ru.practicum.ewm.events.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.model.Event;

import static ru.practicum.ewm.enums.StateAction.CANCEL_REVIEW;
import static ru.practicum.ewm.enums.StateAction.SEND_TO_REVIEW;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "initiatorId", source = "initiatorId")
    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiatorId", source = "userId")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", expression = "java(EventState.PENDING)")
    @Mapping(target = "confirmedRequests", constant = "0")
    @Mapping(target = "views", constant = "0L")
    Event toEntity(NewEventDto dto, Long userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromDto(UpdateEventUserRequest dto, @MappingTarget Event event);

    @AfterMapping
    default void handleStateAction(UpdateEventUserRequest dto,
                                   @MappingTarget Event event) {

        if (dto.getStateAction() == null) {
            return;
        }

        switch (dto.getStateAction()) {
            case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
            case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
        }
    }
}