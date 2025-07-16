package rent.vehicle.useerservice.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rent.vehicle.common.CustomPage;
import rent.vehicle.dto.request.CreateCustomerDto;
import rent.vehicle.dto.request.SearchCustomerRequest;
import rent.vehicle.dto.request.UpdateCustomerDto;
import rent.vehicle.dto.response.CustomerResponse;
import rent.vehicle.useerservice.app.service.CustomerService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class CustomerController {
    final CustomerService customerService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerDto createCustomerDto) {
        return customerService.createCustomer(createCustomerDto);
    }
    @GetMapping("/{userId}")
    public CustomerResponse getCustomer(@PathVariable long  userId) {
        return customerService.getCustomer(userId);
    }
    @PatchMapping("/update/{userId}")
    public CustomerResponse updateCustomer(@PathVariable long userId, @RequestBody UpdateCustomerDto updateCustomerDto) {
        return customerService.updateCustomer(userId, updateCustomerDto);
    }
    @DeleteMapping("/remove/{userId}")
    public CustomerResponse deleteCustomer(@PathVariable long userId) {
        return customerService.removeCustomer(userId);
    }
    @GetMapping("email/{email}")
    public CustomerResponse getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }
    @PostMapping("/search")
    public CustomPage<CustomerResponse> searchCustomers (@RequestBody SearchCustomerRequest searchCustomerRequest) {
        return customerService.searchAllCustomers(searchCustomerRequest);
    }
    @GetMapping("/all")
    public CustomPage<CustomerResponse> getAllActiveCustomers() {
        return customerService.getAllCustomers();
    }
    @GetMapping("/email/users_boxes")
    public CustomPage<CustomerResponse> getUsersByEmailBox(@RequestBody String value){
        return customerService.getCustomersByEmailBox(value);
    }
    @GetMapping("/user/contain/{pattern}")
    public CustomPage<CustomerResponse> getCustomersByNameMatching(@PathVariable String pattern) {
        return customerService.getCustomersByNamePattern(pattern);
    }
}
