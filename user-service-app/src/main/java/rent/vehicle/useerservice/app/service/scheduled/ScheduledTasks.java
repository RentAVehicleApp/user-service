package rent.vehicle.useerservice.app.service.scheduled;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import rent.vehicle.dto.response.CustomerResponse;
import rent.vehicle.enums.CustomerStatus;
import rent.vehicle.useerservice.app.domain.CustomerEntity;
import rent.vehicle.useerservice.app.repository.CustomerRepository;
import rent.vehicle.useerservice.app.service.CustomerService;
import rent.vehicle.useerservice.app.service.CustomerServiceImpl;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final CustomerServiceImpl customerService;
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Jerusalem")
    public void purgeCustomersDaily(){
        log.info("purgeCustomers started");
        customerService.purgeCustomers();
        log.info("purgeCustomersDaily finished");
    }

}
