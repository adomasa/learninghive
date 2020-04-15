package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ChangingPastTrainingDayException;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceDoesNotBelongToUser;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingDayService {

	private final TrainingDayRepository trainingDayRepository;
	private final ObjectiveRepository objectiveRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Transactional
	public TrainingDayResponse addTrainingDay(TrainingDayRequest trainingDayRequest) {
		throwIfDuplicate(trainingDayRequest, -1l);

		// todo don't allow adding training day in the past
		TrainingDay trainingDay = new TrainingDay();
		mountTrainingDay(trainingDay, trainingDayRequest);

		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	@Transactional
	public TrainingDayResponse updateTrainingDay(long id, TrainingDayRequest trainingDayRequest) {
		TrainingDay trainingDay = trainingDayRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class.getSimpleName(), id));

		throwIfDuplicate(trainingDayRequest, id);

		// todo should only be able to edit description
		//if (trainingDay.getScheduledDay().getTime() <= new Date().getTime()) {
		//	throw new ChangingPastTrainingDayException();
		//}

		mountTrainingDay(trainingDay, trainingDayRequest);
		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	public List<TrainingDayResponse> queryTrainingDays(long userId) {
		return trainingDayRepository.findByUserId(userId)
				.parallelStream()
				.map(t -> modelMapper.map(t, TrainingDayResponse.class))
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteTrainingDay(long id) {
		TrainingDay trainingDay = trainingDayRepository
				.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class.getSimpleName(), id));

		if (trainingDay.getScheduledDay().getTime() <= new Date().getTime()) {
			throw new ChangingPastTrainingDayException();
		}

		trainingDayRepository.delete(trainingDay);
	}

	private void throwIfDuplicate(TrainingDayRequest request, Long id) {
		trainingDayRepository.findByScheduledDayAndUserId(request.getScheduledDay(), request.getUserId())
				.ifPresent(t -> {
					if (t.getId() != id) {
						throw new DuplicateResourceException(TrainingDay.class.getSimpleName(),
								"userId and " + "scheduledDay",
								request.getUserId() + " and " + request.getScheduledDay());
					}
				});
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private void mountTrainingDay(TrainingDay destination, TrainingDayRequest source) {
		destination.setDescription(source.getDescription());
		destination.setScheduledDay(source.getScheduledDay());

		User user = userRepository
				.findById(source.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(),
						source.getUserId()));
		destination.setUser(user);

		destination.setObjectives(new ArrayList());
		source.getObjectiveIds()
				.stream()
				.distinct()
				.map(id -> objectiveRepository
						.findById(id)
						.orElseThrow(() -> new ResourceNotFoundException(Objective.class.getSimpleName(), id)))
				.forEach(objective -> {
					if (user.getId() != objective.getUser().getId()) {
						throw new ResourceDoesNotBelongToUser(
								Objective.class.getSimpleName(),
								objective.getId(),
								user.getId());
					}
					destination.getObjectives().add(objective);
				});
	}
}
