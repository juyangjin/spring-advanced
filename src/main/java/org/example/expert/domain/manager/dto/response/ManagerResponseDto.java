package org.example.expert.domain.manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
@AllArgsConstructor
public class ManagerResponseDto {

  private final Long id;
  private final UserResponse user;
}
