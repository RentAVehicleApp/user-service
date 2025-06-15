package rent.vehicle.useerservice.app.service;

import rent.vehicle.dto.request.CreateUserDto;
import rent.vehicle.dto.request.UpdateUserDto;
import rent.vehicle.dto.response.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserDto createUserDto);

    UserResponse getUser(long userId);

    UserResponse updateUser(long userId, UpdateUserDto updateUserDto);

    UserResponse removeUser(long userId);

    UserResponse getUserByEmail(String email);
}
