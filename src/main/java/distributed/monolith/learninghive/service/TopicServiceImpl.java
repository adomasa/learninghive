package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.model.exception.CircularHierarchyException;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceInUseException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.service.util.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
		validateTopicTitle(topicRequest.getTitle());

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
		validateTopicTitle(topicRequest.getTitle());

		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, id));
		ValidatorUtil.validateResourceVersions(topic, topicRequest);

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

	private void mountEntity(Topic destination, TopicRequest source) {
		destination.setTitle(source.getTitle());
		destination.setContent(source.getContent());

		mountChildren(destination, source.getChildrenId());
		mountParent(destination, source.getParentId());
	}

	private void mountParent(Topic target, Long parentId) {
		Topic parent = null;
		if (parentId != null) {
			parent = getUpdatedParentEntity(target, parentId);
		}
		target.setParent(parent);
		removeOutdatedChildReference(target, parentId);
	}

	private void mountChildren(Topic target, List<Long> childrenId) {
		List<Topic> children = null;
		if (childrenId != null) {
			children = getUpdatedChildrenEntities(target, childrenId);
		}
		removeOutdatedParentReferences(target, childrenId);
		target.setChildren(children);
	}

	private List<Topic> getUpdatedChildrenEntities(Topic parent, List<Long> childTopicIds) {
		if (childTopicIds.stream().anyMatch(child -> child == parent.getId())) {
			throw new CircularHierarchyException(Topic.class, parent.getId());
		}

		var newChildTopics = topicRepository.findAllById(childTopicIds);
		validateChildTopicIds(newChildTopics, childTopicIds);

		newChildTopics.forEach(child -> child.setParent(parent));

		return newChildTopics;
	}

	private void validateChildTopicIds(List<Topic> newChildTopicsInDb, List<Long> childTopicIds) {
		if (newChildTopicsInDb.size() != childTopicIds.size()) {
			var presentChildren = newChildTopicsInDb.stream()
					.map(Topic::getId)
					.collect(Collectors.toList());
			var missingChildren = childTopicIds.stream()
					.filter(id -> !presentChildren.contains(id))
					.collect(Collectors.toList());
			throw new ResourceNotFoundException(Topic.class, missingChildren);
		}
	}

	private void validateTopicTitle(String title) {
		if (topicRepository.findByTitle(title).isPresent()) {
			throw new DuplicateResourceException(Topic.class, "title", title);
		}
	}

	private Topic getUpdatedParentEntity(Topic child, Long parentId) {
		if (parentId == child.getId()) {
			throw new CircularHierarchyException(Topic.class, child.getId());
		}

		Topic parent = topicRepository.findById(parentId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, parentId));

		if (parent.getChildren() == null) {
			parent.setChildren(new ArrayList<>());
		}
		parent.getChildren().add(child);

		return parent;
	}

	private void removeOutdatedParentReferences(Topic parent, List<Long> childTopicIds) {
		var oldChildTopics = parent.getChildren();
		if (CollectionUtils.isEmpty(oldChildTopics)) {
			return;
		}

		List<Topic> topicsToUpdate;
		if (CollectionUtils.isEmpty(childTopicIds)) {
			topicsToUpdate = oldChildTopics;
		} else {
			topicsToUpdate = oldChildTopics.stream()
					.filter(oldTopic -> !childTopicIds.contains(oldTopic.getId()))
					.collect(Collectors.toList());
		}
		topicsToUpdate.forEach(childWithoutParent -> childWithoutParent.setParent(null));
	}

	private void removeOutdatedChildReference(Topic target, Long parentId) {
		var oldParent = target.getParent();
		if (oldParent != null && oldParent.getId() != parentId) {
			List<Topic> children = oldParent.getChildren();
			children.remove(target);
		}
	}

}
