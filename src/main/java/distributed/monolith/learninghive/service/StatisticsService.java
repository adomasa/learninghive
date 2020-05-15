package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;

import java.util.List;

public interface StatisticsService {
	List<UsersWithTopicResponse> findUsersWithTopics(long userId);

	List<SubordinatesWithSubCount> countSubordinatesWithTopics(long userId);

	ProgressResponse findSubordinatesProgress(long userId);
}
