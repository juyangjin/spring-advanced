package org.example.expert.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponse;

@Getter
@AllArgsConstructor
public class CommentResponseDto {

  private final Long id;
  private final String contents;
  private final UserResponse user;
}
