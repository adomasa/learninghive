package distributed.monolith.learninghive.model.exception;

import distributed.monolith.learninghive.domain.DeliveryStatus;
import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.model.response.ErrorResponse;
import distributed.monolith.learninghive.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.OptimisticLockException;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionResponseHandler extends ResponseEntityExceptionHandler {
	private final InvitationRepository invitationRepository;
	private static final int RESTRICTION_VIOLATED_STATUS = 432;

	@ExceptionHandler(RestrictionViolationException.class)
	public ResponseEntity<ErrorResponse> handleRestrictionViolatedException(RestrictionViolationException ex) {
		var errorResponse = new ErrorResponse();
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatus(RESTRICTION_VIOLATED_STATUS);

		return ResponseEntity.status(RESTRICTION_VIOLATED_STATUS).body(errorResponse);
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ErrorResponse> handleRestrictionViolatedException(OptimisticLockException ex) {
		var errorResponse = new ErrorResponse();
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setStatus(HttpStatus.CONFLICT.value());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(InvitationDeliveryException.class)
	public void handleInvitationDeliveryException(InvitationDeliveryException ex) {
		var undeliveredInvitation = invitationRepository.findById(ex.getInvitationData().getInvitationId())
				.orElseThrow(() -> new ResourceNotFoundException(Invitation.class,
						ex.getInvitationData().getInvitationId()));
		undeliveredInvitation.setDeliveryStatus(DeliveryStatus.FAILED);
		invitationRepository.save(undeliveredInvitation);
	}
}
