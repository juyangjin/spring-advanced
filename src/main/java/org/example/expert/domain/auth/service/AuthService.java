package org.example.expert.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequestDto;
import org.example.expert.domain.auth.dto.request.SignupRequestDto;
import org.example.expert.domain.auth.dto.response.SigninResponseDto;
import org.example.expert.domain.auth.dto.response.SignupResponseDto;
import org.example.expert.domain.common.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  /*
  회원가입과 로그인은 민감한 정보를 다루기 때문에 불필요한 조회가 가능한 entity가 아닌 Dto 형태로 반환한다.
   */
  @Transactional
  public SignupResponseDto signup(SignupRequestDto signupRequestDto) {

    if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
      throw new InvalidRequestException("이미 존재하는 이메일입니다.");
    }

    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
    UserRole userRole = UserRole.of(signupRequestDto.getUserRole());

    User newUser = new User(signupRequestDto.getEmail(), encodedPassword, userRole);

    User savedUser = userRepository.save(newUser);
    String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

    return new SignupResponseDto(bearerToken);
  }

  public SigninResponseDto signin(SigninRequestDto signinRequest) {

    User user =
        userRepository
            .findByEmail(signinRequest.getEmail())
            .orElseThrow(() -> new InvalidRequestException("가입되지 않은 유저입니다."));

    if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
      throw new AuthException("잘못된 비밀번호입니다.");
    }

    String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

    return new SigninResponseDto(bearerToken);
  }
}
