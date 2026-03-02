package ru.practicum.ewm.comments.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.common.OffsetPageRequest;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Creating comment for user: {}, event: {}", userId, eventId);
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);

        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, author, event));
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);
        log.debug("Comment created with id: {}", comment.getId());

        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        log.info("Updating comment for user: {}, event: {}", userId, eventId);
        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        Comment comment = checkAndGetComment(commentId);

        if (comment.getEvent() != event) {
            throw new ValidationException("This comment is for other event.");
        }

        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        UserShortDto userShort = UserMapper.toUserShortDto(author);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);

        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    public List<CommentDto> getCommentsByAuthorId(Long userId, Integer from, Integer size) {
        log.info("Getting comments from user: userId={}, from={}, size={}", userId, from, size);
        User author = checkAndGetUser(userId);
        UserShortDto userShort = UserMapper.toUserShortDto(author);

        Pageable pageable = new OffsetPageRequest(from, size);

        return commentRepository.findAllByAuthorId(userId, pageable)
                .stream()
                .map(c -> CommentMapper.toCommentDto(c, userShort, EventMapper.toEventShortDto(c.getEvent())))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentByEventId(Long eventId, Integer from, Integer size) {
        log.info("Getting comments for event: eventId={}, from={}, size={}", eventId, from, size);
        Event event = checkAndGetEvent(eventId);
        EventShortDto eventShort = EventMapper.toEventShortDto(event);

        Pageable pageable = new OffsetPageRequest(from, size);

        return commentRepository.findAllByEventId(eventId, pageable)
                .stream()
                .map(c -> CommentMapper.toCommentDto(c, UserMapper.toUserShortDto(c.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("Getting comment with id={}", commentId);
        Comment comment = checkAndGetComment(commentId);
        UserShortDto userShort = UserMapper.toUserShortDto(comment.getAuthor());
        EventShortDto eventShort = EventMapper.toEventShortDto(comment.getEvent());

        return CommentMapper.toCommentDto(comment, userShort, eventShort);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Delete comment by a user: userId={}, commentId={}", userId, commentId);
        Comment comment = checkAndGetComment(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only author can delete the comment.");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Delete comment with id={}", commentId);
        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Comment with id=" + commentId + " was not found"));
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));
    }
}
