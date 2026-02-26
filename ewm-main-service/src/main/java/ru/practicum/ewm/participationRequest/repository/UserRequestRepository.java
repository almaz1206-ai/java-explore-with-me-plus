package ru.practicum.ewm.participationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.participationRequest.model.UserRequest;

import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
    List<UserRequest> findAllByRequesterId(long requesterId);
}