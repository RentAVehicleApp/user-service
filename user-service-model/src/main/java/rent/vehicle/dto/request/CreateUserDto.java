package rent.vehicle.dto.request;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;
import rent.vehicle.enums.UserLicenseType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateUserDto {
    //TODO исправить и совместить с UserEntity
    @NotNull
    private Long id;

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
    private UserLicenseType licenseType;

    @NotBlank
    private String drivingLicenseNumber;

    private List<CreateAdressDto> adresses;

    private Instant createdAt;


}
