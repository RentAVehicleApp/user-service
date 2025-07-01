package rent.vehicle.useerservice.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rent.vehicle.dto.request.CreateUserDto;
import rent.vehicle.dto.request.SearchUserRequest;
import rent.vehicle.dto.request.UpdateUserDto;
import rent.vehicle.dto.response.UserResponse;
import rent.vehicle.useerservice.app.domain.AdressEntity;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserDto createUserDto);

    UserResponse getUser(long userId);

    UserResponse updateUser(long userId, UpdateUserDto updateUserDto);

    UserResponse removeUser(long userId);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> searchUsers(SearchUserRequest searchUserRequest);

    List<AdressEntity> getAdresses(Long userId, Pageable pageable);
}
