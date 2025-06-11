package rent.vehicle.useerservice.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rent.vehicle.enums.UserLicenseType;

import java.time.Instant;
import java.time.LocalDate;
@Getter
@Setter
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false)
    private UserLicenseType licenseType;

    @Column(name = "license_number", nullable = false, unique = true)
    private String drivingLicenseNumber;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // геттеры/сеттеры или Lombok @Getter/@Setter

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

