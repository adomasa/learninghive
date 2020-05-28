package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.stats.ProgressResponse;
import distributed.monolith.learninghive.model.response.stats.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.stats.UsersWithTopicResponse;

import java.util.List;

public interface StatisticsService {
	List<UsersWithTopicResponse> findUsersWithTopics(long userId);

	List<SubordinatesWithSubCount> countSubordinatesWithTopics(long userId);

	ProgressResponse findSubordinatesProgress(long userId);
}
