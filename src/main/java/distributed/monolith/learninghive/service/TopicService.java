package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.exception.DuplicateTitleException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.TopicInUseException;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
	private static final Logger LOG = LoggerFactory.getLogger(TopicService.class);

	private final TopicRepository topicRepository;
	private final EntityManager entityManager;

	public void delete(Long id) {
		Topic topic = topicRepository
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
				.map(TopicResponse::new) //todo temp
				.collect(Collectors.toList());
	}

	public Topic createTopic(TopicRequest topicRequest) {
		if (topicRepository.findByTitle(topicRequest.getTitle()).isPresent()) {
			throw new DuplicateTitleException();
		}

		Topic topic = new Topic();
		mountTopicEntity(topic, topicRequest);

		return topicRepository.save(topic);
	}

	//todo rework
	@Transactional
	public void updateTopic(Long id, TopicRequest topicRequest) {
		Topic topic = topicRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class.getSimpleName()));
		mountTopicEntity(topic, topicRequest);
	}

	public void mountTopicEntity(Topic topic, TopicRequest topicRequest) {
		// todo find out what has been updated in a better way
		if (topicRequest.getTitle() != null) {
			topic.setTitle(topicRequest.getTitle());
		}

		if (topicRequest.getContent() != null) {
			topic.setContent(topicRequest.getContent());
		}

		if (topicRequest.getChildrenId() != null) {
			topic.setChildren(getTopicsFromIdLazy(topicRequest.getChildrenId()));
		}

		if (topicRequest.getParentId() != null) {
			topic.setParent(getTopicFromIdLazy(topicRequest.getParentId()));
		}
	}

	//todo rework
	public List<Topic> getTopicsFromIdLazy(List<Long> idList) {
		return idList
				.parallelStream()
				.map(this::getTopicFromIdLazy)
				.collect(Collectors.toList());
	}

	//todo rework
	public Topic getTopicFromIdLazy(Long id) {
		try {
			return entityManager.getReference(Topic.class, id);
		} catch (IllegalArgumentException e) {
			throw new ResourceNotFoundException(Topic.class.getName() + " " + e.getMessage());
		}
	}
}
