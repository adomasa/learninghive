package distributed.monolith.learninghive.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@Data
public class TrainingDayRequest {
	Long userId;

	@Size(min = 1, max = 100, message = "Title must be 1-100 long")
	String title;

	@Size(max = 500, message = "Description must be 0-500 long")
	String description;

	// todo should maximum amount of topics be configurable?
	@Size(min = 1, max = 4)
	@NotNull
	List<Long> objectiveIds;

	@NotNull
	Date scheduledDay;
}
