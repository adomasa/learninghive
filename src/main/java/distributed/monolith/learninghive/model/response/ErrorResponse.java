package distributed.monolith.learninghive.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
	int status;

	String message;

	LocalDateTime timestamp;
}
