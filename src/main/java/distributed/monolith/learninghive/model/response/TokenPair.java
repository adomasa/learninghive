package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPair {
	private final String jwt;
	@JsonProperty("refresh_token")
	private final String refreshToken;
}
