package rent.vehicle.dto.request;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import rent.vehicle.enums.UserLicenseType;

import java.time.Instant;
import java.time.LocalDate;
@Getter
@Setter
public class CreateUserDto {
    @NotNull
    private long userId;

    @NotBlank
    private String userFirstName;

    @NotBlank
    private String userLastName;

    @NotNull
    @Email
    private String userEmail;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,15}$")
    private String userPhoneNumber;

    @NotNull
    @Past
    @MinAge(16)            // кастомная аннотация, проверяющая, что возраст ≥ 16 лет
    private LocalDate userBirthDate;

    @NotNull
    private UserLicenseType userLicense;

    @NotBlank
    private String drivingLicenseNumber;

    private Instant createdAt;


}
