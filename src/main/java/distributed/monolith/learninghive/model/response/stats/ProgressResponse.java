package distributed.monolith.learninghive.model.response.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProgressResponse {
	String supervisor;
	List<String> plannedTopics;
	List<String> learntTopics;
}
