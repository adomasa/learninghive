package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPair {
	String jwt;
	String refreshToken;
}
