package rent.vehicle.exception;

public class InvalidCustomerAgeException extends RuntimeException {
    public InvalidCustomerAgeException(int mingAge) {
        super(String.format("You must be at least %d years old", mingAge));
    }
}
