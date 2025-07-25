package rent.vehicle.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import rent.vehicle.enums.CustomerLicenseType;

import java.time.Instant;

@Getter
public class UpdateCustomerDto {

    private String userFirstName;
    private String userLastName;
    @Email
    private String userEmail;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String userPhoneNumber;


    private CustomerLicenseType userLicense;


    private Instant updatedAt;


}
