package distributed.monolith.learninghive.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class ObjectiveRequest extends VersionedResourceRequest {
	Long userId;

	@NotNull
	Long topicId;
}
