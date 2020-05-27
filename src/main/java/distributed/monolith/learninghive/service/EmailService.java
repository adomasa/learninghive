package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.DeliveryStatus;
import distributed.monolith.learninghive.domain.Invitation;
import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.exception.InvitationDeliveryException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements InvitationProviderService {
	private static Logger LOG = LoggerFactory.getLogger(EmailService.class);
	private final InvitationRepository invitationRepository;
	private final JavaMailSender javaMailSender;

	private static final String INVITATION_MESSAGE_FORMAT = "Greetings, \n" +
			"You have been invited to LearningHive" +
			"system by %s. Please register using provided link: %s";

	@Override
	public void send(InvitationData invitationData) throws InvitationDeliveryException {
		LOG.info("Sending invitation. Invitation link - {}", invitationData.getLink());

		var message = new SimpleMailMessage();
		message.setTo(invitationData.getRecipient());
		message.setSubject("Invitation to LearningHive system");
		message.setText(String.format(INVITATION_MESSAGE_FORMAT,
				invitationData.getSource(),
				invitationData.getLink()));
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			var undeliveredInvitation = invitationRepository.findById(invitationData.getInvitationId())
					.orElseThrow(() -> new ResourceNotFoundException(Invitation.class,
							invitationData.getInvitationId()));
			LOG.error("Error sending invitation with id {}", invitationData.getInvitationId());
			undeliveredInvitation.setDeliveryStatus(DeliveryStatus.FAILED);
			invitationRepository.save(undeliveredInvitation);

			throw new InvitationDeliveryException(e, this, invitationData);
		}
	}
}
