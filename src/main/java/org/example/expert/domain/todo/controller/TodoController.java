package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.todo.dto.request.TodoSaveRequestDto;
import org.example.expert.domain.todo.dto.response.TodoResponseDto;
import org.example.expert.domain.todo.dto.response.TodoSaveResponseDto;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;

  @PostMapping("/todos")
  public ResponseEntity<TodoSaveResponseDto> saveTodo(
      @Auth AuthUserDto authUser, @Valid @RequestBody TodoSaveRequestDto todoSaveRequestDto) {
    return ResponseEntity.ok(todoService.saveTodo(authUser, todoSaveRequestDto));
  }

  @GetMapping("/todos")
  public ResponseEntity<Page<TodoResponseDto>> getTodos(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(todoService.getTodos(page, size));
  }

  @GetMapping("/todos/{todoId}")
  public ResponseEntity<TodoResponseDto> getTodo(@PathVariable long todoId) {
    return ResponseEntity.ok(todoService.getTodo(todoId));
  }
}
