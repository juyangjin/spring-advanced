package org.example.expert.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequestDto;
import org.example.expert.domain.comment.dto.response.CommentResponseDto;
import org.example.expert.domain.comment.dto.response.CommentSaveResponseDto;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/todos/{todoId}/comments")
  public ResponseEntity<CommentSaveResponseDto> saveComment(
      @Auth AuthUserDto authUser,
      @PathVariable long todoId,
      @Valid @RequestBody CommentSaveRequestDto commentSaveRequestDto) {
    return ResponseEntity.ok(commentService.saveComment(authUser, todoId, commentSaveRequestDto));
  }

  @GetMapping("/todos/{todoId}/comments")
  public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable long todoId) {
    return ResponseEntity.ok(commentService.getComments(todoId));
  }
}
