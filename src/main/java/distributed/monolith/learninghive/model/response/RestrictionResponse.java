package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.Data;

@Data
public class RestrictionResponse {
	long id;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	User userId;

	RestrictionType restrictionType;

	long daysLimit;
}
