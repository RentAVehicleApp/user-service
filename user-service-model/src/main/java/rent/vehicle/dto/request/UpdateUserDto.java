package rent.vehicle.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import rent.vehicle.enums.UserLicenseType;

import java.time.Instant;
import java.time.LocalDate;
@Getter
public class UpdateUserDto {

    private String userFirstName;
    private String userLastName;
    @Email
    private String userEmail;
    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String userPhoneNumber;


    private UserLicenseType userLicense;


    private Instant updatedAt;


}
