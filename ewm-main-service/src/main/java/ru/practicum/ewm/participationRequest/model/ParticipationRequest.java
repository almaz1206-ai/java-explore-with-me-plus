package ru.practicum.ewm.participationRequest.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.enums.StatusRequest;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "userRequests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @ManyToOne
    @Column(name = "event_id")
    private Event event;

    @ManyToOne
    @Column(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private StatusRequest status;
}
