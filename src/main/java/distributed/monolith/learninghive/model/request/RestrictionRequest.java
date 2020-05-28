package distributed.monolith.learninghive.model.request;

import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RestrictionRequest extends VersionedResourceRequest {
	Long userId;

	@NotNull
	RestrictionType restrictionType;

	@NotNull
	Long daysLimit;
}
