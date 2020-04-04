package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.exception.DuplicateTitleException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.TopicInUseException;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
	private static final Logger LOG = LoggerFactory.getLogger(TopicService.class);

	private final TopicRepository topicRepository;
	private final EntityManager entityManager;
	private final ModelMapper modelMapper;

	public void delete(Long id) {
		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName()));
		//todo find out what's expected
		if (topic.getChildren().isEmpty()) {
			topicRepository.deleteById(id);
		} else {
			throw new TopicInUseException();
		}
	}

	public List<TopicResponse> searchByTitlePart(String titlePart) {
		return topicRepository.findByTitleIgnoreCaseContaining(titlePart)
				.parallelStream()
				.map(t -> modelMapper.map(t, TopicResponse.class))
				.collect(Collectors.toList());
	}

	public TopicResponse createTopic(TopicRequest topicRequest) {
		if (topicRepository.findByTitle(topicRequest.getTitle()).isPresent()) {
			throw new DuplicateTitleException();
		}

		var topic = new Topic();
		mountTopicEntity(topic, topicRequest);
		return modelMapper.map(topicRepository.save(topic), TopicResponse.class);
	}

	public TopicResponse updateTopic(Long id, TopicRequest topicRequest) {
		var topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName()));
		mountTopicEntity(topic, topicRequest);

		return modelMapper.map(topicRepository.save(topic), TopicResponse.class);
	}

	/* todo validate topic relations hierarchy
	 *  get all children, get all parents, make sure there are no matches */
	private void mountTopicEntity(Topic destination, TopicRequest source) {
		destination.setTitle(source.getTitle());
		destination.setContent(source.getContent());

		destination.setChildren(source.getChildrenId() == null ?
				null : getTopicsFromIdLazy(source.getChildrenId()));

		destination.setParent(source.getParentId() == null ?
				null : getTopicFromIdLazy(source.getParentId()));
	}

	//todo rework
	private List<Topic> getTopicsFromIdLazy(@NotNull List<Long> idList) {
		return idList
				.parallelStream()
				.map(this::getTopicFromIdLazy)
				.collect(Collectors.toList());
	}

	//todo rework
	private Topic getTopicFromIdLazy(@NotNull Long id) {
		try {
			return entityManager.getReference(Topic.class, id);
		} catch (IllegalArgumentException e) {
			throw new ResourceNotFoundException(e, Topic.class.getName());
		}
	}
}
