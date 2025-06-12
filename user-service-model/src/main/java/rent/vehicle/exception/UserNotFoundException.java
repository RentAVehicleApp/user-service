package rent.vehicle.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(Long userId){
        super(String.format("User with id %d not found", userId));
}

}
