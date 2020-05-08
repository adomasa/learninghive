package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceDoesNotBelongToUser;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.service.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingDayServiceImpl implements TrainingDayService {

	private final TrainingDayRepository trainingDayRepository;
	private final ObjectiveRepository objectiveRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

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
						.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class.getSimpleName(), trainingDayId));

		throwIfDuplicate(trainingDayRequest, trainingDayId);
		DateUtil.throwIfPastDate(trainingDay.getScheduledDay());

		// todo should only be able to edit description

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
						.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class.getSimpleName(), id));

		DateUtil.throwIfPastDate(trainingDay.getScheduledDay());

		trainingDayRepository.delete(trainingDay);
	}

	private void throwIfDuplicate(TrainingDayRequest request, Long id) {
		trainingDayRepository.findByScheduledDayAndUserId(request.getScheduledDay(), request.getUserId())
				.ifPresent(trainingDay -> {
					if (trainingDay.getId() != id) {
						throw new DuplicateResourceException(TrainingDay.class.getSimpleName(),
								"userId and " + "scheduledDay",
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
				.orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(),
						source.getUserId()));
		destination.setUser(user);

		destination.setObjectives(new ArrayList<>());
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
