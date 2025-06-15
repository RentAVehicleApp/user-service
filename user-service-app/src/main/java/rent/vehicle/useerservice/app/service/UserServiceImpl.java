package rent.vehicle.useerservice.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rent.vehicle.dto.request.CreateUserDto;
import rent.vehicle.dto.request.UpdateUserDto;
import rent.vehicle.dto.response.UserResponse;
import rent.vehicle.enums.UserStatus;
import rent.vehicle.exception.UserAlreadyExistsException;
import rent.vehicle.exception.UserNotFoundException;
import rent.vehicle.useerservice.app.domain.UserEntity;
import rent.vehicle.useerservice.app.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {
    final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponse createUser(CreateUserDto createUserDto) {
        log.info("createUser started");
        log.debug("User details: {}", createUserDto);
        if(findUserById(createUserDto.getUserId())==null) {
        validateEmailUniqueness(createUserDto.getUserEmail(), null);
        validatePhoneUniqueness(createUserDto.getUserPhoneNumber(), null);
        try {
            UserEntity userEntity = modelMapper.map(createUserDto, UserEntity.class);
            userRepository.save(userEntity);
            return modelMapper.map(userEntity, UserResponse.class);
        } catch (Exception e) {
            log.error("createUser failed", e);
        }finally {
            log.info("Create user finished");
        }
        }
        return null;
    }

    @Override
    public UserResponse getUser(long userId) {
        log.info("getUser started");
        UserEntity userEntity = userRepository.findUserEntityById(userId);
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Override
    public UserResponse updateUser(long userId, UpdateUserDto updateUserDto) {
        log.info("updateUser started");
        log.debug("User details: {}", updateUserDto);
        try {
            UserEntity userEntity = findUserById(userId);
            if (updateUserDto.getUserFirstName() != null) {
                log.debug("User first name: {}", updateUserDto.getUserFirstName());
                userEntity.setFirstName(updateUserDto.getUserFirstName());
                log.debug("Update first name finished");
            }
            if (updateUserDto.getUserLastName() != null) {
                log.debug("User last name: {}", updateUserDto.getUserLastName());
                userEntity.setLastName(updateUserDto.getUserLastName());
                log.debug("Update last name finished");
            }
            if (updateUserDto.getUserLicense() != null) {
                log.debug("User license {}", updateUserDto.getUserLicense());
                userEntity.setLicenseType(updateUserDto.getUserLicense());
                log.debug("Update license finished");
            }
            if (updateUserDto.getUserEmail() != null) {
                log.debug("User email: {}", updateUserDto.getUserEmail());
                validateEmailUniqueness(updateUserDto.getUserEmail(), userEntity.getId());
                userEntity.setEmail(updateUserDto.getUserEmail());
                log.debug("Update email finished");
            }
            if (updateUserDto.getUserPhoneNumber() != null) {
                log.debug("User phone number: {}", updateUserDto.getUserPhoneNumber());
                validatePhoneUniqueness(updateUserDto.getUserPhoneNumber(), userEntity.getId());
                userEntity.setPhoneNumber(updateUserDto.getUserPhoneNumber());
                log.debug("Update phone number finished");
            }
            userEntity.setUpdatedAt(updateUserDto.getUpdatedAt());
            userRepository.save(userEntity);
            return modelMapper.map(userEntity, UserResponse.class);
        }catch (Exception e) {
            log.error("updateUser failed", e);
        }finally {
            log.info("update user finished");
        }
        return null;
    }

    @Transactional
    @Override
    public UserResponse removeUser(long userId) {
        log.info("removeUser started");
        UserEntity userEntity = findUserById(userId);
        userEntity.setStatus(UserStatus.DELETED);
        userEntity.setUpdatedAt(Instant.from(LocalDateTime.now()));
        log.debug("Removed user details: {}", userEntity);
        userRepository.save(userEntity);
        log.info("remove user finished");
        return modelMapper.map(userEntity, UserResponse.class);
    }

@Transactional
    public List<UserResponse> purgeUsers() {
        log.info("purgeUsers started");
        Instant thirtyDaysAgo = ZonedDateTime.now().minusDays(30).toInstant();
        List<UserEntity> userEntityList = userRepository.findUserEntitiesByStatusContainsAndUpdatedAtBefore(UserStatus.DELETED, thirtyDaysAgo);
        log.debug("Users about to purge: {}", userEntityList);
        userRepository.deleteAll(userEntityList);
        log.info("purgeUsers finished");
        return userEntityList.stream().map(userEntity->modelMapper.map(userEntity, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.info("getUserByEmail started");
        UserEntity userEntity = userRepository.findUserEntityByEmail(email);
        log.debug("User details:{}",userEntity);
        log.info("getUserByEmail finished");
        return modelMapper.map(userEntity, UserResponse.class);
    }

    private void validateEmailUniqueness(String email, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = userRepository.existsUserEntityByEmail(email);
        }else{
            exists = userRepository.existsUserEntityByEmailAndIdNot(email, excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken email: {}", email);
            throw new UserAlreadyExistsException(email); //TODO создать EmailAlreadyRegisteredException
        }
        log.debug("Email {} validation passed", email); //TODO создать безопасность логирования mask for email
        }

    private void validatePhoneUniqueness(String phone, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = userRepository.existsUserEntityByPhoneNumber(phone);
        }else{
            exists = userRepository.existsUserEntityByPhoneNumberAndIdNot(phone,excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken phone number: {}", phone); //TODO создать PhoneNumberAlreadyRegisteredException
        }
        log.debug("Phone {} validation passed", phone);// TODO создать безопасность логирования mask for phone
    }
  private UserEntity findUserById(Long userId){
        return (UserEntity) userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User with" + userId + "not found"));
  }

}
