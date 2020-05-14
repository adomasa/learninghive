package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ChangingPastTrainingDayException;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingDayServiceImpl implements TrainingDayService {

	private final TrainingDayRepository trainingDayRepository;
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final AuthorityService authorityService;


	@Override
	@Transactional
	public TrainingDayResponse addTrainingDay(TrainingDayRequest trainingDayRequest) {
		// second parameter is only important when updating existing entity
		throwIfDuplicate(trainingDayRequest, -1L);

		// todo don't allow adding training day in the past
		TrainingDay trainingDay = new TrainingDay();
		mountEntity(trainingDay, trainingDayRequest);

		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	@Override
	@Transactional
	public TrainingDayResponse updateTrainingDay(long trainingDayId, TrainingDayRequest trainingDayRequest) {
		TrainingDay trainingDay = trainingDayRepository
				.findById(trainingDayId)
				.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class, trainingDayId));

		throwIfDuplicate(trainingDayRequest, trainingDayId);

		// todo should only be able to edit description
		//if (trainingDay.getScheduledDay().getTime() <= new Date().getTime()) {
		//	throw new ChangingPastTrainingDayException();
		//}

		mountEntity(trainingDay, trainingDayRequest);
		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TrainingDayResponse> queryTrainingDays(long userId) {
		return trainingDayRepository.findByUserId(userId)
				.parallelStream()
				.map(t -> modelMapper.map(t, TrainingDayResponse.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteTrainingDay(long id) {
		TrainingDay trainingDay = trainingDayRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class, id));

		if (trainingDay.getScheduledDay().getTime() <= new Date().getTime()) {
			throw new ChangingPastTrainingDayException();
		}

		trainingDayRepository.delete(trainingDay);
	}

	private void throwIfDuplicate(TrainingDayRequest request, Long id) {
		trainingDayRepository.findByScheduledDayAndUserId(request.getScheduledDay(), request.getUserId())
				.ifPresent(trainingDay -> {
					if (trainingDay.getId() != id) {
						throw new DuplicateResourceException(TrainingDay.class,
								"userId and scheduledDay",
								request.getUserId() + " and " + request.getScheduledDay());
					}
				});
	}

	private void mountEntity(TrainingDay destination, TrainingDayRequest source) {
		destination.setTitle(source.getTitle());
		destination.setDescription(source.getDescription());
		destination.setScheduledDay(source.getScheduledDay());

		User user = userRepository
				.findById(source.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(User.class, source.getUserId()));
		destination.setUser(user);

		destination.setTopics(new ArrayList<>());
		source.getTopicIds()
				.stream()
				.distinct()
				.map(id -> topicRepository
						.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException(Topic.class, id)))
				.forEach(topic -> destination.getTopics().add(topic));
	}
}
