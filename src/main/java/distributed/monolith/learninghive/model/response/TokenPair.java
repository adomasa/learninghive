package distributed.monolith.learninghive.model.response;

import lombok.Value;

@Value
public class TokenPair {
	String jwt;
	String refreshToken;
}
