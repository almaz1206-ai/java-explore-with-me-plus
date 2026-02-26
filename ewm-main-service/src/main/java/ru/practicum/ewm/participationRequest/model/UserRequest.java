package ru.practicum.ewm.participationRequest.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.participationRequest.enums.StatusRequest;

import java.time.LocalDateTime;

@Entity
@Table(name = "userRequests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime localDateTime;

    @ManyToOne
    @Column(name = "event_id")
    private Event event;

    @ManyToOne
    @Column(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private StatusRequest status;
}
