package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.request.VersionedResourceRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingDayResponse extends VersionedResourceRequest {
	Long id;
	String title;
	String description;
	Date scheduledDate;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	User user;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	List<Topic> topics;
}
