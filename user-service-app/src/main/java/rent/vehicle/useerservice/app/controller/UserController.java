package rent.vehicle.useerservice.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rent.vehicle.dto.request.CreateUserDto;
import rent.vehicle.dto.request.SearchUserRequest;
import rent.vehicle.dto.request.UpdateUserDto;
import rent.vehicle.dto.response.UserResponse;
import rent.vehicle.useerservice.app.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    final UserService userService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }
    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable long  userId) {
        return userService.getUser(userId);
    }
    @PatchMapping("/update/{userId}")
    public UserResponse updateUser(@PathVariable long userId, @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(userId,updateUserDto);
    }
    @DeleteMapping
    public UserResponse deleteUser(@PathVariable long userId) {
        return userService.removeUser(userId);
    }
    @GetMapping("email/{email}")
    public UserResponse getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
    @PostMapping("/search")
    public Page<UserResponse> searchUsers (@RequestBody SearchUserRequest searchUserRequest) {
        return userService.searchUsers(searchUserRequest);
    }
}
