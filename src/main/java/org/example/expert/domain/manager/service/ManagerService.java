package org.example.expert.domain.manager.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequestDto;
import org.example.expert.domain.manager.dto.response.ManagerResponseDto;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponseDto;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

  private final ManagerRepository managerRepository;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  @Transactional
  public ManagerSaveResponseDto saveManager(
      AuthUserDto authUser, long todoId, ManagerSaveRequestDto managerSaveRequestDto) {
    User user = User.fromAuthUser(authUser);
    Todo todo =
        todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("일정이 존재하지 않습니다."));

    if (!ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
      throw new InvalidRequestException("담당자를 등록하려고 하는 유저와 일정을 만든 유저가 유효하지 않습니다.");
    }

    User managerUser =
        userRepository
            .findById(managerSaveRequestDto.getManagerUserId())
            .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

    if (ObjectUtils.nullSafeEquals(user.getId(), managerUser.getId())) {
      throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
    }

    Manager newManagerUser = new Manager(managerUser, todo);
    Manager savedManagerUser = managerRepository.save(newManagerUser);

    return new ManagerSaveResponseDto(
        savedManagerUser.getId(), new UserResponse(managerUser.getId(), managerUser.getEmail()));
  }

  public List<ManagerResponseDto> getManagers(long todoId) {
    Todo todo =
        todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("일정이 존재하지 않습니다."));

    List<Manager> managerList = managerRepository.findAllByTodoId(todo.getId());

    List<ManagerResponseDto> dtoList = new ArrayList<>();
    for (Manager manager : managerList) {
      User user = manager.getUser();
      dtoList.add(
          new ManagerResponseDto(manager.getId(), new UserResponse(user.getId(), user.getEmail())));
    }
    return dtoList;
  }

  // 유저에 대한 예외처리 멘트가 일반유저와 일정을 작성한 유저의 구분이 없는데 괜찮은가?
  @Transactional
  public void deleteManager(long userId, long todoId, long managerId) {
    Manager manager =
        managerRepository
            .findById(managerId)
            .orElseThrow(() -> new InvalidRequestException("관리자가 존재하지 않습니다."));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new InvalidRequestException("해당 유저가 존재하지 않습니다."));

    Todo todo =
        todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("해당 유저가 존재하지 않습니다."));

    if (todo.getUser() == null
        || !ObjectUtils.nullSafeEquals(user.getId(), todo.getUser().getId())) {
      throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.");
    }

    if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
      throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
    }

    managerRepository.delete(manager);
  }
}
