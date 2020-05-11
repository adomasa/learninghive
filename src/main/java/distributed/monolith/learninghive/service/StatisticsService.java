package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.TeamTopicProgressResponse;
import distributed.monolith.learninghive.model.response.TeamsWithTopicResponse;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;

import java.util.List;

public interface StatisticsService {
	List<UsersWithTopicResponse> findUsersWithTopics();

	List<TeamsWithTopicResponse> findTeamsWithTopics();

	List<TeamTopicProgressResponse> findTeamsTopicProgress();
}
