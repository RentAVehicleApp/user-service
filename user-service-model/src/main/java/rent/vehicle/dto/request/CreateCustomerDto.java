package rent.vehicle.dto.request;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import rent.vehicle.enums.CustomerLicenseType;

import java.time.Instant;
import java.time.LocalDate;
@Getter
@Setter
public class CreateCustomerDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String phoneNumber;

    @NotNull
    @Past
   // @MinAge(16)            // кастомная аннотация, проверяющая, что возраст ≥ 16 лет
    private LocalDate birthDate;

    @NotNull
    private CustomerLicenseType licenseType;

    @NotBlank
    private String drivingLicenseNumber;

    private Instant createdAt;


}
