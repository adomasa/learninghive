package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessToken {
	private final String jwt;
}
