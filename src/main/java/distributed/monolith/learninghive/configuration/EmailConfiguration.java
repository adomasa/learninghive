package distributed.monolith.learninghive.configuration;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("email")
public class EmailConfiguration {
	private String username;
	private String password;
	private String host;

	@Bean
	public JavaMailSender javaMailSender() {
		var mailSender = new JavaMailSenderImpl();

		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setHost(host);
		mailSender.setPort(587);

		var props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		return mailSender;
	}
}
