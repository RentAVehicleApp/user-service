package rent.vehicle.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {
private Instant timeStamp;
private int status;
private String error;
private String message;
private String path;
}
