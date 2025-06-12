package rent.vehicle.dto.response;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import rent.vehicle.enums.UserLicenseType;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class UserResponse {
    private long userId;

    private String userFirstName;
    private String userLastName;

    private String userEmail;
    private String userPhoneNumber;

    private LocalDate userBirthDate;

    private UserLicenseType userLicense;
    private String drivingLicenseNumber;

    private Instant createdAt;
    private Instant updatedAt;

}
