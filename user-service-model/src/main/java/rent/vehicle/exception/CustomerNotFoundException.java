package rent.vehicle.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
    public CustomerNotFoundException(Long userId){
        super(String.format("User with id %d not found", userId));
}

}
