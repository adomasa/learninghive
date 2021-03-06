package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ChangingPastTrainingDayException;
import distributed.monolith.learninghive.model.exception.DuplicateResourceException;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.exception.RestrictionViolationException;
import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.repository.RestrictionRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import distributed.monolith.learninghive.restrictions.RestrictionValidator;
import distributed.monolith.learninghive.service.util.DateUtil;
import distributed.monolith.learninghive.service.util.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingDayServiceImpl implements TrainingDayService {

	private final TrainingDayRepository trainingDayRepository;
	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final RestrictionValidator restrictionValidator;
	private final RestrictionRepository restrictionRepository;
	private final ModelMapper modelMapper;

	private final AuthorityService authorityService;

	@Override
	@Transactional
	public TrainingDayResponse addTrainingDay(TrainingDayRequest trainingDayRequest) {
		authorityService.validateLoggedUserOrSupervisorOf(trainingDayRequest.getUserId());
		// second parameter is only important when updating existing entity
		throwIfDuplicate(trainingDayRequest, -1L);
		throwIfPastDate(trainingDayRequest.getScheduledDay());

		TrainingDay trainingDay = new TrainingDay();
		mountEntity(trainingDay, trainingDayRequest);

		List<TrainingDay> trainingDays = trainingDayRepository.findByUserId(trainingDay.getUser().getId());
		throwIfViolatesRestrictions(trainingDays, trainingDay);
		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	@Override
	@Transactional
	public TrainingDayResponse updateTrainingDay(long trainingDayId, TrainingDayRequest trainingDayRequest) {
		authorityService.validateLoggedUserOrSupervisorOf(trainingDayRequest.getUserId());
		TrainingDay trainingDay = trainingDayRepository
				.findById(trainingDayId)
				.orElseThrow(() -> new ResourceNotFoundException(TrainingDay.class, trainingDayId));

		throwIfDuplicate(trainingDayRequest, trainingDayId);

		// Only allow editing description of past training days
		if (isAnyNotDescriptionFieldChanged(trainingDay, trainingDayRequest)) {
			throwIfPastDate(trainingDay.getScheduledDay());
		}

		var oldTrainingDayDate = trainingDay.getScheduledDay();
		mountEntity(trainingDay, trainingDayRequest);
		ValidatorUtil.validateResourceVersions(trainingDay, trainingDayRequest);

		if (!DateUtil.areEqual(trainingDayRequest.getScheduledDay(), oldTrainingDayDate)) {
			List<TrainingDay> trainingDays = trainingDayRepository.findByIdNotAndUserId(trainingDay.getId(),
					trainingDay.getUser().getId());
			throwIfViolatesRestrictions(trainingDays, trainingDay);
		}

		return modelMapper.map(trainingDayRepository.save(trainingDay), TrainingDayResponse.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<TrainingDayResponse> queryTrainingDays(long userId) {
		authorityService.validateLoggedUserOrSupervisorOf(userId);

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
		throwIfPastDate(trainingDay.getScheduledDay());

		trainingDayRepository.delete(trainingDay);
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

	private void throwIfDuplicate(TrainingDayRequest request, Long id) {
		trainingDayRepository.findByScheduledDayAndUserId(request.getScheduledDay(), request.getUserId())
				.ifPresent(trainingDay -> {
					if (trainingDay.getId() != id) {
						throw new DuplicateResourceException(TrainingDay.class,
								"userId and " + "scheduledDay",
								request.getUserId() + " and " + request.getScheduledDay());
					}
				});
	}

	private void throwIfPastDate(Date date) {
		if (DateUtil.isPastDate(date)) {
			throw new ChangingPastTrainingDayException();
		}
	}

	private void throwIfViolatesRestrictions(List<TrainingDay> existingTrainingDays, TrainingDay trainingDay) {
		List<Restriction> restrictions = findApplicableRestrictions(trainingDay.getUser().getId());

		Restriction restriction = restrictionValidator.findViolatedRestriction(existingTrainingDays,
				trainingDay, restrictions);
		if (restriction != null) {
			throw new RestrictionViolationException(restriction);
		}
	}

	private boolean isAnyNotDescriptionFieldChanged(TrainingDay trainingDay, TrainingDayRequest request) {
		if (!DateUtil.areEqual(trainingDay.getScheduledDay(), request.getScheduledDay())
				|| !trainingDay.getTitle().equals(request.getTitle())
				|| trainingDay.getUser().getId() != request.getUserId()) {
			return true;
		}

		var topicIds = trainingDay.getTopics()
				.stream()
				.map(t -> t.getId())
				.sorted()
				.collect(Collectors.toList());

		return !topicIds.equals(request.getTopicIds()
				.stream()
				.distinct()
				.sorted()
				.collect(Collectors.toList()));
	}

	private List<Restriction> findApplicableRestrictions(Long userId) {
		List<Restriction> restrictions = restrictionRepository.findByUserIdOrUserIdIsNull(userId);

		// Remove global restrictions if there is a user specific restriction of same type
		restrictions.stream()
				.filter(r -> r.getUser() == null)
				.collect(Collectors.toList())
				.forEach(globalRestriction -> {
					if (restrictions.stream()
							.anyMatch(r -> r.getUser() != null
									&& r.getRestrictionType() == globalRestriction.getRestrictionType())) {
						restrictions.remove(globalRestriction);
					}
				});

		return restrictions;
	}
}
