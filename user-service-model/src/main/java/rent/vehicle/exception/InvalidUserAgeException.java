package rent.vehicle.exception;

public class InvalidUserAgeException extends RuntimeException {
    public InvalidUserAgeException(int mingAge) {
        super(String.format("You must be at least %d years old", mingAge));
    }
}
