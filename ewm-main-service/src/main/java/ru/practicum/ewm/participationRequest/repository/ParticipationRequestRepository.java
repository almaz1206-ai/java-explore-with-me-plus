package ru.practicum.ewm.participationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participationRequest.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(long requesterId);
}