package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.*;
import distributed.monolith.learninghive.model.exception.CircularHierarchyException;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceInUseException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.LearnedTopicsResponse;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

	private final ObjectiveRepository objectiveRepository;
	private final TopicRepository topicRepository;
	private final LearnedTopicRepository learnedTopicRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	@Transactional
	public TopicResponse createTopic(TopicRequest topicRequest) {
		if (topicRepository.findByTitle(topicRequest.getTitle()).isPresent()) {
			throw new DuplicateResourceException(Topic.class, "title", topicRequest.getTitle());
		}

		var topic = new Topic();
		mountEntity(topic, topicRequest);
		topic = topicRepository.saveAndFlush(topic);

		if (topicRepository.isCircularHierarchy(topic.getId())) {
			throw new CircularHierarchyException(Topic.class);
		}

		return modelMapper.map(topic, TopicResponse.class);
	}

	@Override
	@Transactional
	public TopicResponse updateTopic(Long id, TopicRequest topicRequest) {
		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, id));
		mountEntity(topic, topicRequest);
		topic = topicRepository.saveAndFlush(topic);

		if (topicRepository.isCircularHierarchy(topic.getId())) {
			throw new CircularHierarchyException(Topic.class, topic.getId());
		}

		return modelMapper.map(topic, TopicResponse.class);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!objectiveRepository.findByTopicId(id).isEmpty()) {
			throw new ResourceInUseException(Topic.class, id, Objective.class);
		}

		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, id));

		if (!topic.getTrainingDays().isEmpty()) {
			throw new ResourceInUseException(Topic.class, id, TrainingDay.class);
		}

		learnedTopicRepository.deleteByTopicId(id);

		if (topic.getChildren().isEmpty()) {
			Topic parentTopic = topic.getParent();
			if (parentTopic != null) {
				parentTopic.getChildren().remove(topic);
			}
			topicRepository.deleteById(id);
		} else {
			throw new ResourceInUseException(Topic.class, id, Topic.class);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TopicResponse> searchByTitlePart(String titlePart) {
		return topicRepository.findByTitleIgnoreCaseContaining(titlePart)
				.parallelStream()
				.map(t -> modelMapper.map(t, TopicResponse.class))
				.collect(Collectors.toList());
	}

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

	private void mountEntity(Topic destination, TopicRequest source) {
		destination.setTitle(source.getTitle());
		destination.setContent(source.getContent());

		List<Topic> children = null;
		if (source.getChildrenId() != null) {
			children = getUpdatedChildrenEntities(destination, source.getChildrenId());
		}
		destination.setChildren(children);

		Topic parent = null;
		if (source.getParentId() != null) {
			parent = getUpdatedParentEntity(destination, source.getParentId());
		}
		destination.setParent(parent);
	}

	private List<Topic> getUpdatedChildrenEntities(Topic parent, List<Long> childrenId) {
		List<Topic> children = topicRepository.findAllById(childrenId);
		if (children.size() != childrenId.size()) {
			List<Long> presentChildren = children.stream()
					.map(Topic::getId)
					.collect(Collectors.toList());
			List<Long> missingChildren = childrenId.stream()
					.filter(id -> !presentChildren.contains(id))
					.collect(Collectors.toList());
			throw new ResourceNotFoundException(Topic.class, missingChildren);
		}
		children.forEach(child -> child.setParent(parent));
		return children;
	}

	private Topic getUpdatedParentEntity(Topic child, Long parentId) {
		if (parentId == child.getId()) {
			throw new CircularHierarchyException(Topic.class, child.getId());
		}
		Topic parent = topicRepository.findById(parentId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, parentId));
		parent.getChildren().add(child);

		return parent;
	}

}
