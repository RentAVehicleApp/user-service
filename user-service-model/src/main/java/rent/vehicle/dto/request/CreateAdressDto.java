package rent.vehicle.dto.request;

import lombok.Getter;
import lombok.Setter;
import rent.vehicle.dto.response.UserResponse;
@Getter
@Setter
public class CreateAdressDto {
    private Long id;
    private String streetName;
    private String city;
    private String apartmentNumber;
    private String postalCode;
    private UserResponse userResponse;
}
