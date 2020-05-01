package distributed.monolith.learninghive.service;

import org.springframework.mail.MailException;

public interface EmailService {
	void sendEmail(String recipient, String subject, String text) throws MailException;
}
