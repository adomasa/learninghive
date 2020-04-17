package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceInUseException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

	private final ObjectiveRepository objectiveRepository;
	private final TopicRepository topicRepository;
	private final ModelMapper modelMapper;

	@Transactional
	public TopicResponse createTopic(TopicRequest topicRequest) {
		if (topicRepository.findByTitle(topicRequest.getTitle()).isPresent()) {
			throw new DuplicateResourceException(Topic.class.getSimpleName(), "title", topicRequest.getTitle());
		}

		var topic = new Topic();
		mountTopicEntity(topic, topicRequest);

		return modelMapper.map(topicRepository.save(topic), TopicResponse.class);
	}

	@Transactional
	public TopicResponse updateTopic(Long id, TopicRequest topicRequest) {
		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName(), id));
		mountTopicEntity(topic, topicRequest);

		return modelMapper.map(topicRepository.save(topic), TopicResponse.class);
	}

	@Transactional
	public void delete(Long id) {
		if (!objectiveRepository.findByTopicId(id).isEmpty()) {
			throw new ResourceInUseException(Topic.class.getSimpleName(), id, Topic.class.getSimpleName());
		}

		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName(), id));

		//todo find out what's expected
		if (topic.getChildren().isEmpty()) {
			Topic parentTopic = topic.getParent();
			if (parentTopic != null) {
				parentTopic.getChildren().remove(topic);
			}
			topicRepository.deleteById(id);
		} else {
			throw new ResourceInUseException(Topic.class.getSimpleName(), id, Topic.class.getSimpleName());
		}
	}

	public List<TopicResponse> searchByTitlePart(String titlePart) {
		return topicRepository.findByTitleIgnoreCaseContaining(titlePart)
				.parallelStream()
				.map(t -> modelMapper.map(t, TopicResponse.class))
				.collect(Collectors.toList());
	}

	/* todo validate topic relations hierarchy */
	private void mountTopicEntity(Topic destination, TopicRequest source) {
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
			throw new ResourceNotFoundException(Topic.class.getSimpleName(), missingChildren);
		}
		children.forEach(child -> child.setParent(parent));
		return children;
	}

	private Topic getUpdatedParentEntity(Topic child, Long parentId) {
		Topic parent = topicRepository.findById(parentId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName(), parentId));
		parent.getChildren().add(child);

		return parent;
	}

}
