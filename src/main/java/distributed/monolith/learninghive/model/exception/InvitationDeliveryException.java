package distributed.monolith.learninghive.model.exception;

import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.service.InvitationProviderService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InvitationDeliveryException extends RuntimeException {

	private static final long serialVersionUID = 6431058435683738701L;

	@Getter
	private final InvitationData invitationData;

	public InvitationDeliveryException(Exception cause,
	                                   InvitationProviderService invitationService,
	                                   InvitationData invitationData) {
		super(String.format("Error on delivering invite to recipient %s using %s.",
				invitationData.getRecipient(), invitationService.getClass().getSimpleName()), cause);
		this.invitationData = invitationData;
	}
}
