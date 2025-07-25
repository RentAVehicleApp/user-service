package rent.vehicle.useerservice.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rent.vehicle.common.CustomPage;
import rent.vehicle.constants.ApiPaths;
import rent.vehicle.dto.request.CreateCustomerDto;
import rent.vehicle.dto.request.GenericSearchRequest;
import rent.vehicle.dto.request.UpdateCustomerDto;
import rent.vehicle.dto.response.CustomerResponse;
import rent.vehicle.useerservice.app.service.CustomerService;

@RestController
@RequestMapping(ApiPaths.PATH_BASE)
@RequiredArgsConstructor
@Validated
public class CustomerController {
    final CustomerService customerService;

    @GetMapping(ApiPaths.PATH_HEALTH)
    public String health() {
        return "OK";
    }
    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        return customerService.createCustomer(createCustomerDto);
    }
    @GetMapping(ApiPaths.PATH_ID)
    public CustomerResponse getCustomer(@PathVariable long  customerId) {
        return customerService.getCustomer(customerId);
    }
    @PatchMapping(ApiPaths.PATH_ID)
    public CustomerResponse updateCustomer(@PathVariable long customerId, @RequestBody UpdateCustomerDto updateCustomerDto) {
        return customerService.updateCustomer(customerId, updateCustomerDto);
    }
    @DeleteMapping(ApiPaths.PATH_ID)
    public CustomerResponse deleteCustomer(@PathVariable long customerId) {
        return customerService.removeCustomer(customerId);
    }
    @GetMapping(ApiPaths.PATH_EMAIL+ApiPaths.PATH_EMAIL_VAR)
    public CustomerResponse getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }
    @GetMapping(ApiPaths.PATH_SEARCH)
    public CustomPage<CustomerResponse> searchCustomers (@ModelAttribute GenericSearchRequest genericSearchRequest) {
        return customerService.searchAllCustomers(genericSearchRequest);
    }
    @GetMapping(ApiPaths.PATH_ALL)
    public CustomPage<CustomerResponse> getAllActiveCustomers() {
        return customerService.getAllCustomers();
    }
    @PostMapping(ApiPaths.PATH_EMAIL+ApiPaths.PATH_USERS_BOXES+ApiPaths.PATH_PATTERN)
    public CustomPage<CustomerResponse> getCustomersByEmailBox(@PathVariable String pattern){
        return customerService.getCustomersByEmailBox(pattern);
    }
    @GetMapping(ApiPaths.PATH_CUSTOMER+ApiPaths.PATH_CONTAIN+ApiPaths.PATH_PATTERN)
    public CustomPage<CustomerResponse> getCustomersByNameMatching(@PathVariable String pattern) {
        return customerService.getCustomersByNamePattern(pattern);
    }
}
