package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
	private final String email;
	private final String name;
}
