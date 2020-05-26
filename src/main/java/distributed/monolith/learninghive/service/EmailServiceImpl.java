package distributed.monolith.learninghive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender javaMailSender;

	@Override
	public void sendEmail(String recipient, String subject, String text) throws MailException {
		var message = new SimpleMailMessage();
		message.setTo(recipient);
		message.setSubject(subject);
		message.setText(text);

//		javaMailSender.send(message);
	}
}
