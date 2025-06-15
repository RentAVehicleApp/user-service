package rent.vehicle.useerservice.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rent.vehicle.dto.request.CreateUserDto;
import rent.vehicle.dto.request.SearchUserRequest;
import rent.vehicle.dto.request.UpdateUserDto;
import rent.vehicle.dto.response.UserResponse;
import rent.vehicle.enums.UserStatus;
import rent.vehicle.exception.UserAlreadyExistsException;
import rent.vehicle.exception.UserNotFoundException;
import rent.vehicle.exception.UserPhoneNumberAlreadyRegistered;
import rent.vehicle.useerservice.app.domain.UserEntity;
import rent.vehicle.useerservice.app.repository.UserRepository;
import rent.vehicle.useerservice.app.service.specification.UserSpecificationBuilder;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static rent.vehicle.useerservice.app.service.specification.UserSpecification.isNotDeleted;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserSpecificationBuilder userSpecificationBuilder;

    @Transactional
    @Override
    public UserResponse createUser(CreateUserDto createUserDto) {
        log.info("createUser started");
        log.debug("User details: {}", createUserDto);
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

        return null;
    }

    @Override
    public UserResponse getUser(long userId) {
        log.info("getUser started");
        UserEntity userEntity = userRepository.findUserEntityById(userId);
        return modelMapper.map(userEntity, UserResponse.class);
    }
    @Transactional
    @Override
    public UserResponse updateUser(long userId, UpdateUserDto updateUserDto) {
        log.info("updateUser started");
        log.debug("User details: {}", updateUserDto);
        try {
            UserEntity userEntity = (UserEntity) findUserById(userId);
            updateFirstNameIfPresent(updateUserDto, userEntity);
            updateLastNameIfPresent(updateUserDto, userEntity);
            updateEmailIfPresent(updateUserDto, userEntity);
            updatePhoneNumberIfPresent(updateUserDto, userEntity);
            updateLycenseTypeIfPresent(updateUserDto, userEntity);
            userEntity.setUpdatedAt(Instant.now());
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
        UserEntity userEntity = (UserEntity)findUserById(userId);
        userEntity.setStatus(UserStatus.DELETED);
        userEntity.setUpdatedAt(Instant.now());
        log.debug("Removed user details: {}", userEntity);
        userRepository.save(userEntity);
        log.info("remove user finished");
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Transactional
    public List<UserResponse> purgeUsers() { //TODO добавить права(роли) на вызов //TODO вынести в другой ScheduledService
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

    public Page searchUsers(SearchUserRequest searchUserRequest) {
        log.info("searchUsers started");
        Pageable pageable = PageRequest.of(
                searchUserRequest.getPage(),
                searchUserRequest.getSize(),
                Sort.by(searchUserRequest.getSort().split(","))
        );
        Specification<UserEntity> spec = isNotDeleted();

        Specification<UserEntity> searchSpec = userSpecificationBuilder.buildFromRequest(searchUserRequest);

        if (searchSpec != null) {
            spec = spec.and(searchSpec);
        }
        Page<UserEntity> userPage = userRepository.findAll(spec, pageable);

        // 6. Преобразуем Entity в Response DTO
        return modelMapper.map(userPage, Page.class);
    }

    private void updateFirstNameIfPresent(UpdateUserDto updateUserDto, UserEntity user) {
        if(Optional.ofNullable(updateUserDto.getUserFirstName()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        user.setFirstName(user.getFirstName());
        log.info("updateFirstNameIfPresent finished");
    }
    private void updateLastNameIfPresent(UpdateUserDto updateUserDto, UserEntity user) {
        if(Optional.ofNullable(updateUserDto.getUserLastName()).isEmpty()) {
            return;
        }
        log.info("updateLastName started");
        user.setLastName(user.getLastName());
        log.info("updateLastName finished");
    }
    private void updateEmailIfPresent(UpdateUserDto updateUserDto, UserEntity user) {
        if(Optional.ofNullable(updateUserDto.getUserEmail()).isEmpty()) {
            return;
        }
        log.info("updateEmail started");
        validateEmailUniqueness(updateUserDto.getUserEmail(), user.getId());
        user.setEmail(user.getEmail());
        log.info("updateEmail finished");
    }
    private void updatePhoneNumberIfPresent(UpdateUserDto updateUserDto, UserEntity user) {
        if(Optional.ofNullable(updateUserDto.getUserPhoneNumber()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        validatePhoneUniqueness(updateUserDto.getUserPhoneNumber(), user.getId());
        user.setPhoneNumber(user.getPhoneNumber());
        log.info("updatePhoneNumber finished");
    }
    private void updateLycenseTypeIfPresent(UpdateUserDto updateUserDto, UserEntity user) {
        if(Optional.ofNullable(updateUserDto.getUserLicense()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        user.setLicenseType(user.getLicenseType());
    }

    private void validateEmailUniqueness(String email, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = userRepository.existsUserEntityByEmail(email);
        }else{
            exists = userRepository.existsUserEntityByEmailAndIdNot(email, excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken email: {}", maskEmail(email));
            throw new UserAlreadyExistsException(maskEmail(email));
        }
        log.debug("Email {} validation passed", maskEmail(email));
    }

    private void validatePhoneUniqueness(String phone, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = userRepository.existsUserEntityByPhoneNumber(phone);
        }else{
            exists = userRepository.existsUserEntityByPhoneNumberAndIdNot(phone,excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken phone number: {}", maskPhone(phone));
            throw new UserPhoneNumberAlreadyRegistered(maskPhone(phone));
        }
        log.debug("Phone {} validation passed", maskPhone(phone));// TODO создать безопасность логирования mask for phone
    }
    private UserEntity findUserById(Long userId){
        return (UserEntity) userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("User with " + userId + " not found"));
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if(atIndex > 2){
            return email.substring(0,2) + "***" + email.substring(atIndex);
        }
        return "***@***";
    }

    private String maskPhone(String phone) { //TODO доделать проверки на разные страны, убрать хардкод
        if(phone!=null&&phone.length()>10){
            return phone.substring(0,3) + "****" + phone.substring(phone.length()-4);
        }
        return null;
    }


}



