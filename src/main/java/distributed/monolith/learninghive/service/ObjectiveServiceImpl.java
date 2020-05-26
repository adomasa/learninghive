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
import distributed.monolith.learninghive.security.SecurityService;
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

	private final AuthorityService authorityService;
	private final SecurityService securityService;

	@Override
	public ObjectiveResponse updateObjective(long id, ObjectiveRequest objectiveRequest) {
		authorityService.validateLoggedUserOrSupervisorOf(objectiveRequest.getUserId());
		var objective = objectiveRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class, id));

		authorityService.validateLoggedUserOrSupervisorOf(objective.getUser().getId());

		// todo is there any point to allow updating user or topic
		mountEntity(objective, objectiveRequest);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	private void setObjectiveOwner(Objective objective) {
		var owner = userRepository.findById(securityService.getLoggedUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						securityService.getLoggedUserId())
				);
		objective.setOwner(owner);
	}

	@Override
	public ObjectiveResponse addObjective(ObjectiveRequest objectiveRequest) {
		authorityService.validateLoggedUserOrSupervisorOf(objectiveRequest.getUserId());

		if (objectiveRepository.findByUserIdAndTopicId(
				objectiveRequest.getUserId(), objectiveRequest.getTopicId()) != null) {
			throw new DuplicateResourceException(Objective.class,
					"user and topic ids",
					objectiveRequest.getUserId() + " and " + objectiveRequest.getTopicId());
		}

		var objective = new Objective();
		mountEntity(objective, objectiveRequest);
		setObjectiveOwner(objective);

		return modelMapper.map(objectiveRepository.save(objective), ObjectiveResponse.class);
	}

	@Override
	public void deleteObjective(long id) {
		var objective = objectiveRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(Objective.class, id));
		authorityService.validateLoggedUserOrSupervisorOf(objective.getUser().getId());

		objectiveRepository.delete(objective);
	}


	@Transactional(readOnly = true)
	@Override
	public List<ObjectiveResponse> findByUserId(long userId) {
		authorityService.validateLoggedUserOrSupervisorOf(userId);

		return objectiveRepository.findByUserId(userId)
				.parallelStream()
				.map(o -> modelMapper.map(o, ObjectiveResponse.class))
				.collect(Collectors.toList());
	}

	@Override
	public void mountEntity(Objective destination, ObjectiveRequest source) {
		var topic = topicRepository.findById(source.getTopicId())
				.orElseThrow(() -> new ResourceNotFoundException(
								Topic.class,
								source.getTopicId()
						)
				);
		destination.setTopic(topic);

		var user = userRepository.findById(source.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(
								User.class,
								source.getUserId()
						)
				);
		destination.setUser(user);
	}
}
