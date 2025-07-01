package rent.vehicle.dto.request;

import lombok.Getter;
import rent.vehicle.dto.response.UserResponse;
@Getter
public class AdressResponse {
    private Long id;
    private String streetName;
    private String city;
    private String apartmentNumber;
    private String postalCode;
    private UserResponse userResponse;
}
