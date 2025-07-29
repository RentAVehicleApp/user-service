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
import rent.vehicle.common.CustomPage;
import rent.vehicle.dto.request.CreateCustomerDto;
import rent.vehicle.dto.request.GenericSearchRequest;
import rent.vehicle.dto.request.UpdateCustomerDto;
import rent.vehicle.dto.response.CustomerResponse;
import rent.vehicle.enums.CustomerStatus;
import rent.vehicle.exception.CustomerAlreadyExistsException;
import rent.vehicle.exception.CustomerNotFoundException;
import rent.vehicle.exception.CustomerPhoneNumberAlreadyRegistered;
import rent.vehicle.useerservice.app.common.SearchCriteriaParser;
import rent.vehicle.useerservice.app.domain.CustomerEntity;
import rent.vehicle.useerservice.app.repository.CustomerRepository;
import rent.vehicle.useerservice.app.service.specification.CustomerSpecification;
import rent.vehicle.useerservice.app.service.specification.CustomerSpecificationBuilder;


import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final CustomerSpecificationBuilder<CustomerEntity> customerSpecificationBuilder;
    private final SearchCriteriaParser searchCriteriaParser;

    @Transactional
    @Override
    public CustomerResponse createCustomer(CreateCustomerDto createCustomerDto) {
        log.info("createCustomer started");
        log.debug("User details: {}", createCustomerDto);
        validateEmailUniqueness(createCustomerDto.getEmail(), null);
        validatePhoneUniqueness(createCustomerDto.getPhoneNumber(), null);
        try {
            CustomerEntity customerEntity = modelMapper.map(createCustomerDto, CustomerEntity.class);
            customerRepository.save(customerEntity);
            return modelMapper.map(customerEntity, CustomerResponse.class);
        } catch (Exception e) {
            log.error("createCustomer failed", e);
        }finally {
            log.info("Create user finished");
        }

        return null;
    }

    @Override
    public CustomerResponse getCustomer(long userId) {
        CustomerEntity customerEntity = findCustomerById(userId); // уже выбросит исключение если не найден
        return modelMapper.map(customerEntity, CustomerResponse.class);
    }
    @Transactional
    @Override
    public CustomerResponse updateCustomer(long userId, UpdateCustomerDto updateCustomerDto) {
        log.info("updateCustomer started");
        log.debug("User details: {}", updateCustomerDto);
        try {
            CustomerEntity customerEntity =  findCustomerById(userId);
            updateFirstNameIfPresent(updateCustomerDto, customerEntity);
            updateLastNameIfPresent(updateCustomerDto, customerEntity);
            updateEmailIfPresent(updateCustomerDto, customerEntity);
            updatePhoneNumberIfPresent(updateCustomerDto, customerEntity);
            updateLycenseTypeIfPresent(updateCustomerDto, customerEntity);
            customerEntity.setUpdatedAt(Instant.now());
            customerRepository.save(customerEntity);
            return modelMapper.map(customerEntity, CustomerResponse.class);
        }catch (Exception e) {
            log.error("updateCustomer failed", e);
        }finally {
            log.info("update user finished");
        }
        return null;
    }

    @Transactional
    @Override
    public CustomerResponse removeCustomer(long userId) {
        log.info("removeCustomer started");
        CustomerEntity customerEntity = findCustomerById(userId);
        customerEntity.setStatus(CustomerStatus.DELETED);
        customerEntity.setUpdatedAt(Instant.now());
        log.debug("Removed user details: {}", customerEntity);
        customerRepository.save(customerEntity);
        log.info("remove user finished");
        return modelMapper.map(customerEntity, CustomerResponse.class);
    }

    @Transactional
    public List<CustomerResponse> purgeCustomers() { //TODO добавить права(роли) на вызов //TODO вынести в другой ScheduledService
        log.info("purgeCustomers started");
        Instant thirtyDaysAgo = ZonedDateTime.now().minusDays(30).toInstant();
        List<CustomerEntity> customerEntityList = customerRepository.findUserEntitiesByStatusAndUpdatedAtBefore(CustomerStatus.DELETED, thirtyDaysAgo);
        log.debug("Users about to purge: {}", customerEntityList);
        customerRepository.deleteAll(customerEntityList);
        log.info("purgeCustomers finished");
        return customerEntityList.stream().map(customerEntity ->modelMapper.map(customerEntity, CustomerResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerByEmail(String email) {
        log.info("getCustomerByEmail started");
        CustomerEntity customerEntity = customerRepository.findUserEntityByEmail(email);
        log.debug("User details:{}", customerEntity);
        log.info("getCustomerByEmail finished");
        return modelMapper.map(customerEntity, CustomerResponse.class);
    }

    public CustomPage<CustomerResponse> searchAllCustomers(String filter,Pageable pageable) {
        GenericSearchRequest req = searchCriteriaParser.buildSearchRequest(filter,pageable);
        // Разбор sort-параметра
        String[] parts = req.getSort().split(",");
        String sortField = parts[0];
        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1].trim())
                : Sort.Direction.ASC; // по умолчанию ASC, если не указано

        Sort sort = Sort.by(new Sort.Order(direction, sortField));

        pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

        Specification<CustomerEntity> spec = customerSpecificationBuilder.buildFromRequest(req);
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);
        CustomPage<CustomerEntity> customPage  = CustomPage.from(page);
        return customPage.map(entity -> modelMapper.map(entity, CustomerResponse.class));
    }

    @Override
    public CustomPage<CustomerResponse> getAllCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<CustomerEntity> spec = CustomerSpecification.isNotDeleted();
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);
        CustomPage<CustomerEntity> customPage  = CustomPage.from(page);
        return customPage.map(entity -> modelMapper.map(entity, CustomerResponse.class));
    }

    @Override
    public CustomPage<CustomerResponse> getCustomersByEmailBox(String value) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<CustomerEntity> spec = CustomerSpecification.emailEndWith(value);
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);
        CustomPage<CustomerEntity> customPage  = CustomPage.from(page);
        return customPage.map(entity -> modelMapper.map(entity, CustomerResponse.class));
    }

    @Override
    public CustomPage<CustomerResponse> getCustomersByNamePattern(String pattern) {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<CustomerEntity> spec = CustomerSpecification.nameContains(pattern);
        Page<CustomerEntity> page = customerRepository.findAll(spec, pageable);
        CustomPage<CustomerEntity> customPage  = CustomPage.from(page);
        return customPage.map(entity -> modelMapper.map(entity, CustomerResponse.class));
    }


    private void updateFirstNameIfPresent(UpdateCustomerDto updateCustomerDto, CustomerEntity user) {
        if(Optional.ofNullable(updateCustomerDto.getUserFirstName()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        user.setFirstName(updateCustomerDto.getUserFirstName());
        log.info("updateFirstNameIfPresent finished");
    }
    private void updateLastNameIfPresent(UpdateCustomerDto updateCustomerDto, CustomerEntity user) {
        if(Optional.ofNullable(updateCustomerDto.getUserLastName()).isEmpty()) {
            return;
        }
        log.info("updateLastName started");
        user.setLastName(updateCustomerDto.getUserLastName());
        log.info("updateLastName finished");
    }
    private void updateEmailIfPresent(UpdateCustomerDto updateCustomerDto, CustomerEntity user) {
        if(Optional.ofNullable(updateCustomerDto.getUserEmail()).isEmpty()) {
            return;
        }
        log.info("updateEmail started");
        validateEmailUniqueness(updateCustomerDto.getUserEmail(), user.getId());
        user.setEmail(updateCustomerDto.getUserEmail());
        log.info("updateEmail finished");
    }
    private void updatePhoneNumberIfPresent(UpdateCustomerDto updateCustomerDto, CustomerEntity user) {
        if(Optional.ofNullable(updateCustomerDto.getUserPhoneNumber()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        validatePhoneUniqueness(updateCustomerDto.getUserPhoneNumber(), user.getId());
        user.setPhoneNumber(updateCustomerDto.getUserPhoneNumber());
        log.info("updatePhoneNumber finished");
    }
    private void updateLycenseTypeIfPresent(UpdateCustomerDto updateCustomerDto, CustomerEntity user) {
        if(Optional.ofNullable(updateCustomerDto.getUserLicense()).isEmpty()) {
            return;
        }
        log.info("updateFirstNameIfPresent started");
        user.setLicenseType(updateCustomerDto.getUserLicense());
    }

    private void validateEmailUniqueness(String email, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = customerRepository.existsUserEntityByEmail(email);
        }else{
            exists = customerRepository.existsUserEntityByEmailAndIdNot(email, excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken email: {}", maskEmail(email));
            throw new CustomerAlreadyExistsException(maskEmail(email));
        }
        log.debug("Email {} validation passed", maskEmail(email));
    }

    private void validatePhoneUniqueness(String phone, Long excludeUserId ){
        boolean exists;
        if(excludeUserId == null){
            exists = customerRepository.existsUserEntityByPhoneNumber(phone);
        }else{
            exists = customerRepository.existsUserEntityByPhoneNumberAndIdNot(phone,excludeUserId);
        }
        if(exists){
            log.warn("Attempt to use already taken phone number: {}", maskPhone(phone));
            throw new CustomerPhoneNumberAlreadyRegistered(maskPhone(phone));
        }
        log.debug("Phone {} validation passed", maskPhone(phone));// TODO создать безопасность логирования mask for phone
    }
    private CustomerEntity findCustomerById(Long userId){
        return customerRepository.findById(userId)
                .orElseThrow(()-> new CustomerNotFoundException("User with " + userId + " not found"));
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



