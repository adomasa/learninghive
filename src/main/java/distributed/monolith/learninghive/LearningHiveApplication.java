package distributed.monolith.learninghive;

import distributed.monolith.learninghive.configuration.YamlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@SpringBootApplication
public class LearningHiveApplication {
	@Autowired
	private YamlConfiguration configuration;

	public static void main(String[] args) {
		SpringApplication.run(LearningHiveApplication.class, args);
	}

	/**
	 * Hard coded english locale
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}

	@Bean
	public JavaMailSender javaMailSender() {
		var mailSender = new JavaMailSenderImpl();

		var emailConfig = configuration.getEmail();
		mailSender.setUsername(emailConfig.getUsername());
		mailSender.setPassword(emailConfig.getPassword());
		mailSender.setHost(emailConfig.getHost());
		mailSender.setPort(587);

		var props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		return mailSender;
	}
}
