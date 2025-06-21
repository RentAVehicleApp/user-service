package rent.vehicle.dto.response;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rent.vehicle.enums.UserLicenseType;
import rent.vehicle.enums.UserRole;
import rent.vehicle.enums.UserStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;  // Изменено с userId на id
    private String firstName;  // Изменено с userFirstName
    private String lastName;   // Изменено с userLastName
    private String email;      // Изменено с userEmail
    private String phoneNumber; // Изменено с userPhoneNumber
    private LocalDate birthDate; // Изменено с userBirthDate
    private UserLicenseType licenseType; // Изменено с userLicense
    private String drivingLicenseNumber; // Оставлено как есть
    private Instant createdAt;
    private Instant updatedAt;
    private UserStatus status; // Добавьте статус
    private List<UserRole> roles; // Добавьте роли
}