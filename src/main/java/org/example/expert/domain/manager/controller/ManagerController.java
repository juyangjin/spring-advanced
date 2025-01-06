package org.example.expert.domain.manager.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequestDto;
import org.example.expert.domain.manager.dto.response.ManagerResponseDto;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponseDto;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ManagerController {

  private final ManagerService managerService;
  private final JwtUtil jwtUtil;

  @PostMapping("/todos/{todoId}/managers")
  public ResponseEntity<ManagerSaveResponseDto> saveManager(
      @Auth AuthUserDto authUser,
      @PathVariable long todoId,
      @Valid @RequestBody ManagerSaveRequestDto managerSaveRequestDto) {
    return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequestDto));
  }

  @GetMapping("/todos/{todoId}/managers")
  public ResponseEntity<List<ManagerResponseDto>> getMembers(@PathVariable long todoId) {
    return ResponseEntity.ok(managerService.getManagers(todoId));
  }

  @DeleteMapping("/todos/{todoId}/managers/{managerId}")
  public void deleteManager(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable long todoId,
      @PathVariable long managerId) {
    Claims claims = jwtUtil.extractClaims(bearerToken.substring(7));
    long userId = Long.parseLong(claims.getSubject());
    managerService.deleteManager(userId, todoId, managerId);
  }
}
