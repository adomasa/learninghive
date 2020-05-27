package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.exception.InvitationDeliveryException;

public interface InvitationProviderService {
	void send(InvitationData invitationData) throws InvitationDeliveryException;
}
