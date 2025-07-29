package rent.vehicle.useerservice.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UserServiceAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceAppApplication.class, args);
    }

}
