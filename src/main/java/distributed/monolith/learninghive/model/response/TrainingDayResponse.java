package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class TrainingDayResponse {
	Long id;
	String description;
	Date scheduledDate;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	User user;

	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	List<Topic> objectives;
}
