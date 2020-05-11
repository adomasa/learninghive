package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeamsWithTopicResponse {
	String topic;
	List<TeamResponse> teams;
}
