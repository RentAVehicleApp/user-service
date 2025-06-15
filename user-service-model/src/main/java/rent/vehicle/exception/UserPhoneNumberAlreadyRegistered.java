package rent.vehicle.exception;

public class UserPhoneNumberAlreadyRegistered extends RuntimeException {
    public UserPhoneNumberAlreadyRegistered(String phoneNumber) {
        super(String.format("User with phone number %s already exists", phoneNumber));
    }
}
