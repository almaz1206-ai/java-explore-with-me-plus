package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsByAuthorId(Long userId, Integer from, Integer size);

    List<CommentDto> getCommentByEventId(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);
}
