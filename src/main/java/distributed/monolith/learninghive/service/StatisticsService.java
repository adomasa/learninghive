package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;

public interface StatisticsService {
	UsersWithTopicResponse findUsersWithTopics(long topicId, long userId);

	SubordinatesWithSubCount countSubordinatesWithTopics(long topicId, long userId);

	ProgressResponse findSubordinatesProgress(long userId);
}
