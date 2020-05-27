package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.exception.InvitationDeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements InvitationProviderService {
	private final JavaMailSender javaMailSender;
	private static final String INVITATION_MESSAGE_FORMAT = "Greetings, \n" +
			"You have been invited to LearningHive" +
			"system by %s. Please register using provided link: %s";

	@Override
	public void send(InvitationData invitationData) throws InvitationDeliveryException {
		var message = new SimpleMailMessage();
		message.setTo(invitationData.getRecipient());
		message.setSubject("Invitation to LearningHive system");
		message.setText(String.format(INVITATION_MESSAGE_FORMAT,
				invitationData.getSource(),
				invitationData.getLink()));
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			throw new InvitationDeliveryException(e, this, invitationData);
		}
	}
}
