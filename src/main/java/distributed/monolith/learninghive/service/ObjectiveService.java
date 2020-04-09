package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateObjectiveException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.response.ObjectiveResponse;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObjectiveService {

	private final ObjectiveRepository objectiveRepository;
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public ObjectiveResponse updateObjective(long id, ObjectiveRequest objectiveRequest) {
		Objective objective = objectiveRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class.getSimpleName(), id));

		// todo is there any point to allow updating user or topic
		mountObjectiveEntity(objective, objectiveRequest);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	public ObjectiveResponse addObjective(ObjectiveRequest objectiveRequest) {
		if (objectiveRepository.findByUserIdAndTopicId(
				objectiveRequest.getUserId(), objectiveRequest.getTopicId()) != null) {
			throw new DuplicateObjectiveException();
		}

		Objective objective = new Objective();
		mountObjectiveEntity(objective, objectiveRequest);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	public void deleteObjective(long id) {
		// todo don't delete if used in training day
		objectiveRepository.deleteById(id);
	}

	public List<ObjectiveResponse> queryByUserId(long userId) {
		return objectiveRepository.findByUserId(userId)
				.parallelStream()
				.map(o -> modelMapper.map(o, ObjectiveResponse.class))
				.collect(Collectors.toList());
	}

	private void mountObjectiveEntity(Objective destination, ObjectiveRequest source) {
		destination.setCompleted(source.getCompleted());

		Topic topic = topicRepository.findById(source.getTopicId())
				.orElseThrow(() -> new ResourceNotFoundException(
								Topic.class.getSimpleName(),
								source.getTopicId()
						)
				);
		destination.setTopic(topic);

		User user = userRepository.findById(source.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
								User.class.getSimpleName(),
								source.getUserId()
						)
				);
		destination.setUser(user);
	}
}
