package distributed.monolith.learninghive.model.exception;

import distributed.monolith.learninghive.model.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {
	private static int restrictionViolatedStatus = 432;

	@ExceptionHandler(RestrictionViolationException.class)
	public ResponseEntity<ErrorResponse> handleRestrictionViolatedException(RestrictionViolationException ex) {
		var errorResponse = new ErrorResponse();

		errorResponse.setTimestamp(LocalDateTime.now());
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatus(restrictionViolatedStatus);

		return ResponseEntity.status(restrictionViolatedStatus).body(errorResponse);
	}

}
