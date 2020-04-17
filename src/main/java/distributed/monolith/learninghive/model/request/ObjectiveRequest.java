package distributed.monolith.learninghive.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ObjectiveRequest {
	Long userId;

	@NotNull
	Long topicId;

	@NotNull
	Boolean completed;
}
