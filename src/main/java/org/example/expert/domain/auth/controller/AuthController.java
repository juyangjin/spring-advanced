package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.auth.dto.request.SigninRequestDto;
import org.example.expert.domain.auth.dto.request.SignupRequestDto;
import org.example.expert.domain.auth.dto.response.SigninResponseDto;
import org.example.expert.domain.auth.dto.response.SignupResponseDto;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/auth/signup")
  public SignupResponseDto signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
    return authService.signup(signupRequestDto);
  }

  @PostMapping("/auth/signin")
  public SigninResponseDto signin(@Valid @RequestBody SigninRequestDto signinRequestDto) {
    return authService.signin(signinRequestDto);
  }
}
