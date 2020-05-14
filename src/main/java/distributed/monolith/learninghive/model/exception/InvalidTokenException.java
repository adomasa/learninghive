package distributed.monolith.learninghive.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
	private static final long serialVersionUID = -1458815167089353006L;
	private static final String ERROR_FORMAT = "Invalid %s token: %s";
	@Getter
	private final Type type;

	public InvalidTokenException(InvalidTokenException.Type type, String token) {
		super(String.format(ERROR_FORMAT, type.getLabel(), token));
		this.type = type;
	}

	public enum Type {
		INVITATION("invitation"),
		REFRESH("refresh");

		private final String label;


		Type(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	}
}
