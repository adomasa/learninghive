package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;

import java.util.List;

public interface TopicService {
	TopicResponse createTopic(TopicRequest topicRequest);

	TopicResponse updateTopic(Long id, TopicRequest topicRequest);

	void delete(Long id);

	List<TopicResponse> searchByTitlePart(String titlePart);

	void mountEntity(Topic destination, TopicRequest source);
}
