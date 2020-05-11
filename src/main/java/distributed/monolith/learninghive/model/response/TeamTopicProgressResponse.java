package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamTopicProgressResponse {
	String supervisor;
	List<String> plannedTopics;
	List<String> learntTopics;
}
