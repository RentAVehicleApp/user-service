package rent.vehicle.exception;

public class CustomerPhoneNumberAlreadyRegistered extends RuntimeException {
    public CustomerPhoneNumberAlreadyRegistered(String phoneNumber) {
        super(String.format("User with phone number %s already exists", phoneNumber));
    }
}
