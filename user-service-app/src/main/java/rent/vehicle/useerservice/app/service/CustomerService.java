package rent.vehicle.useerservice.app.service;

import rent.vehicle.common.CustomPage;
import rent.vehicle.dto.request.CreateCustomerDto;
import rent.vehicle.dto.request.GenericSearchRequest;
import rent.vehicle.dto.request.UpdateCustomerDto;
import rent.vehicle.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerDto createCustomerDto);

    CustomerResponse getCustomer(long userId);

    CustomerResponse updateCustomer(long userId, UpdateCustomerDto updateCustomerDto);

    CustomerResponse removeCustomer(long userId);

    CustomerResponse getCustomerByEmail(String email);

    CustomPage<CustomerResponse> searchAllCustomers(GenericSearchRequest searchUserRequest);

    CustomPage<CustomerResponse> getAllCustomers();

    CustomPage<CustomerResponse> getCustomersByEmailBox(String value);

    CustomPage<CustomerResponse> getCustomersByNamePattern(String pattern);
}
