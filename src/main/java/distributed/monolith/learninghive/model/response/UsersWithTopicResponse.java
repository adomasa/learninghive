package distributed.monolith.learninghive.model.response;

import lombok.Data;

import java.util.List;

@Data
public class UsersWithTopicResponse {
	String topic;
	List<String> users;
}
