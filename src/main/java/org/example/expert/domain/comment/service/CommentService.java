package org.example.expert.domain.comment.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequestDto;
import org.example.expert.domain.comment.dto.response.CommentResponseDto;
import org.example.expert.domain.comment.dto.response.CommentSaveResponseDto;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

  private final TodoRepository todoRepository;
  private final CommentRepository commentRepository;

  @Transactional
  public CommentSaveResponseDto saveComment(
      AuthUserDto authUser, long todoId, CommentSaveRequestDto commentSaveRequestDto) {
    User user = User.fromAuthUser(authUser);
    Todo todo =
        todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("일정이 존재하지 않습니다."));

    Comment newComment = new Comment(commentSaveRequestDto.getContents(), user, todo);

    Comment savedComment = commentRepository.save(newComment);

    return new CommentSaveResponseDto(
        savedComment.getId(),
        savedComment.getContents(),
        new UserResponse(user.getId(), user.getEmail()));
  }

  public List<CommentResponseDto> getComments(long todoId) {
    List<Comment> commentList = commentRepository.findAllByTodoId(todoId);
    List<CommentResponseDto> dtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      dtoList.add(toCommentResponseDto(comment));
    }
    return dtoList;
  }

  private CommentResponseDto toCommentResponseDto(Comment comment) {
    User user = comment.getUser();
    return new CommentResponseDto(
        comment.getId(), comment.getContents(), new UserResponse(user.getId(), user.getEmail()));
  }
}
