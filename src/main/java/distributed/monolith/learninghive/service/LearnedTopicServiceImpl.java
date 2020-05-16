package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.LearnedTopic;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.response.LearnedTopicsResponse;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearnedTopicServiceImpl implements LearnedTopicService {

	private final TopicRepository topicRepository;
	private final LearnedTopicRepository learnedTopicRepository;
	private final UserRepository userRepository;

	@Override
	public void createLearnedTopic(long topicId, long userId) {
		if (learnedTopicRepository.findByUserIdAndTopicId(userId, topicId).isPresent()) {
			return;
		}

		Topic topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, topicId));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

		LearnedTopic learnedTopic = new LearnedTopic();
		learnedTopic.setTopic(topic);
		learnedTopic.setUser(user);
		learnedTopicRepository.save(learnedTopic);
	}

	@Override
	public void deleteLearnedTopic(long topicId, long userId) {
		learnedTopicRepository.findByUserIdAndTopicId(userId, topicId)
				.ifPresent(learnedTopicRepository::delete);
	}

	@Override
	public LearnedTopicsResponse findLearnedTopics(long userId) {
		LearnedTopicsResponse response = new LearnedTopicsResponse();
		response.setTopics(learnedTopicRepository.findByUserId(userId)
				.stream()
				.map(LearnedTopic::getTopic)
				.collect(Collectors.toList()));
		return response;
	}

}
