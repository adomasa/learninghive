package distributed.monolith.learninghive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender javaMailSender;

	public void sendEmail(String recipient, String subject, String text) throws MailException {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(recipient);
		msg.setSubject(subject);
		msg.setText(text);

		javaMailSender.send(msg);
	}
}
