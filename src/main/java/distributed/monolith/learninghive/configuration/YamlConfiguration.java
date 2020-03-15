package distributed.monolith.learninghive.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class YamlConfiguration {
	private EmailConfiguration email;

	public EmailConfiguration getEmail() {
		return email;
	}

	public void setEmail(EmailConfiguration value) {
		email = value;
	}
}
