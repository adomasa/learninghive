package distributed.monolith.learninghive.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class VersionedResourceRequest {
	Integer version;
}
