package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.response.ObjectiveResponse;
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
public class ObjectiveServiceImpl implements ObjectiveService {

	private final ObjectiveRepository objectiveRepository;
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public ObjectiveResponse updateObjective(long id, ObjectiveRequest objectiveRequest) {
		var objective = objectiveRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class.getSimpleName(), id));

		// todo is there any point to allow updating user or topic
		mountEntity(objective, objectiveRequest);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	@Override
	public ObjectiveResponse addObjective(ObjectiveRequest objectiveRequest) {
		if (objectiveRepository.findByUserIdAndTopicId(
				objectiveRequest.getUserId(), objectiveRequest.getTopicId()) != null) {
			throw new DuplicateResourceException(Objective.class.getSimpleName(),
					"user and topic ids",
					objectiveRequest.getUserId() + " and " + objectiveRequest.getTopicId());
		}

		var objective = new Objective();
		mountEntity(objective, objectiveRequest);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	@Override
	public void deleteObjective(long id) {
		var objective = objectiveRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class.getSimpleName(), id));

		objectiveRepository.delete(objective);
	}


	@Transactional(readOnly = true)
	@Override
	public List<ObjectiveResponse> findByUserId(long userId) {
		return objectiveRepository.findByUserId(userId)
				.parallelStream()
				.map(o -> modelMapper.map(o, ObjectiveResponse.class))
				.collect(Collectors.toList());
	}

	@Override
	public void mountEntity(Objective destination, ObjectiveRequest source) {
		var topic = topicRepository.findById(source.getTopicId())
				.orElseThrow(() -> new ResourceNotFoundException(
								Topic.class.getSimpleName(),
								source.getTopicId()
						)
				);
		destination.setTopic(topic);

		var user = userRepository.findById(source.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
								User.class.getSimpleName(),
								source.getUserId()
						)
				);
		destination.setUser(user);
	}
}
