package org.example.expert.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.enums.UserRole;

@Getter
@AllArgsConstructor
public class AuthUserDto {
  private final Long id;
  private final String email;
  private final UserRole userRole;
}
