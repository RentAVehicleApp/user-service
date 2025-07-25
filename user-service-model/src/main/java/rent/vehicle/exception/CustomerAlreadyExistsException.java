package rent.vehicle.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
