package distributed.monolith.learninghive.model.request;

import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RestrictionRequest {
	Long userId;

	@NotNull
	RestrictionType restrictionType;

	@NotNull
	Long daysLimit;
}
