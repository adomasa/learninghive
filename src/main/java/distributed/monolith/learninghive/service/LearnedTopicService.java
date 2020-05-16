package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.LearnedTopicsResponse;

public interface LearnedTopicService {
	void createLearnedTopic(long topicId, long userId);

	void deleteLearnedTopic(long topicId, long userId);

	LearnedTopicsResponse findLearnedTopics(long userId);
}
