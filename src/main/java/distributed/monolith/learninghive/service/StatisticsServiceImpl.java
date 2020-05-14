package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.TrainingDay;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinateLearnedCount;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.TrainingDayRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final TopicRepository topicRepository;
	private final UserRepository userRepository;
	private final LearnedTopicRepository learnedTopicRepository;
	private final TrainingDayRepository trainingDayRepository;

	@Override
	public UsersWithTopicResponse findUsersWithTopics(long topicId, long userId) {
		var supervisor = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
		var topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, topicId));

		List<String> names = new ArrayList<>();

		List<User> subordinates = supervisor.getSubordinates();

		for (User subordinate : subordinates) {
			learnedTopicRepository.findByUserIdAndTopicId(subordinate.getId(), topicId)
					.ifPresent(learnedTopic -> names.add(learnedTopic.getUser().getName()));
		}

		return new UsersWithTopicResponse(topic.getTitle(), names);
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	@Override
	public SubordinatesWithSubCount countSubordinatesWithTopics(long topicId, long userId) {
		var supervisor = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
		var topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException(Topic.class, topicId));

		List<User> subordinatesWithEmployees =
				supervisor.getSubordinates()
						.stream()
						.filter(user -> !user.getSubordinates().isEmpty())
						.collect(Collectors.toList());

		SubordinatesWithSubCount subordinatesWithSubCount = new SubordinatesWithSubCount();
		subordinatesWithSubCount.setTopic(topic.getTitle());


		List<SubordinateLearnedCount> subordinateLearnedCounts = new ArrayList<>();
		for (User subordinateThatSupervises : subordinatesWithEmployees) {
			int count = 0;
			List<User> subordinates = subordinateThatSupervises.getSubordinates();

			for (User subordinate : subordinates) {
				if (learnedTopicRepository.findByUserIdAndTopicId(subordinate.getId(), topicId).isPresent()) {
					count++;
				}
			}

			subordinateLearnedCounts.add(new SubordinateLearnedCount(subordinateThatSupervises.getName(),
					count,
					subordinateThatSupervises.getSubordinates().size()));
		}

		subordinatesWithSubCount.setSubordinates(subordinateLearnedCounts);
		return subordinatesWithSubCount;
	}

	@Override
	public ProgressResponse findSubordinatesProgress(long userId) {
		var supervisor = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
		Set<String> plannedTopics = new HashSet<>();
		Set<String> learntTopics = new HashSet<>();

		supervisor.getSubordinates()
				.forEach(user -> learnedTopicRepository.findByUserId(user.getId())
						.forEach(learnedTopic -> learntTopics.add(learnedTopic.getTopic().getTitle())));


		for (User user : supervisor.getSubordinates()) {
			plannedTopics.addAll(getNamesOfTopics(getPlannedTrainingDays(getUsersTrainingDays(user.getId()))));
		}

		return new ProgressResponse(supervisor.getName(),
				new ArrayList<>(plannedTopics),
				new ArrayList<>(learntTopics));
	}

	private List<String> getNamesOfTopics(List<TrainingDay> trainingDays) {
		List<String> namesOfTopics = new ArrayList<>();
		for (TrainingDay trainingDay : trainingDays) {
			trainingDay.getTopics().forEach(topic -> namesOfTopics.add(topic.getTitle()));
		}
		return namesOfTopics;
	}

	private List<TrainingDay> getPlannedTrainingDays(List<TrainingDay> usersTrainingDays) {
		List<TrainingDay> plannedTrainingDays = new ArrayList<>();
		for (TrainingDay trainingDay : usersTrainingDays) {
			if (trainingDay.getScheduledDay().after(Date.valueOf(LocalDate.now()))) {
				plannedTrainingDays.add(trainingDay);
			}
		}
		return plannedTrainingDays;
	}

	private List<TrainingDay> getUsersTrainingDays(long id) {
		return trainingDayRepository.findByUserId(id);
	}
}
