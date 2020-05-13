package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinateLearnedCount;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.repository.*;
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
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						userId
				));
		var topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException(
						Topic.class.getSimpleName(),
						topicId
				));

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
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						userId
				));
		var topic = topicRepository.findById(topicId)
				.orElseThrow(() -> new ResourceNotFoundException(
						Topic.class.getSimpleName(),
						topicId
				));

		List<User> subordinatesThatSupervise =
				supervisor.getSubordinates()
						.stream()
						.filter(user -> !user.getSubordinates().isEmpty())
						.collect(Collectors.toList());

		SubordinatesWithSubCount subordinatesWithSubCount = new SubordinatesWithSubCount();
		subordinatesWithSubCount.setTopic(topic.getTitle());

		int count = 0;
		List<SubordinateLearnedCount> subordinateLearnedCounts = new ArrayList<>();
		for (User subordinateThatSupervises : subordinatesThatSupervise) {
			List<User> subordinates = subordinateThatSupervises.getSubordinates();

			for (User subordinate : subordinates) {
				if (learnedTopicRepository.findByUserIdAndTopicId(subordinate.getId(), topicId).isPresent()) {
					count++;
				}
			}

			subordinateLearnedCounts.add(new SubordinateLearnedCount(subordinateThatSupervises.getName(),
					count,
					subordinateThatSupervises.getSubordinates().size()));
			count = 0;
		}

		subordinatesWithSubCount.setSubordinates(subordinateLearnedCounts);
		return subordinatesWithSubCount;
	}

	@Override
	public ProgressResponse findSubordinatesProgress(long userId) {
		var supervisor = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class.getSimpleName(),
						userId
				));
		Set<String> plannedTopics = new HashSet<>();
		Set<String> learntTopics = new HashSet<>();

		supervisor.getSubordinates()
				.forEach(user -> learnedTopicRepository.findByUserId(user.getId())
						.forEach(learnedTopic -> learntTopics.add(learnedTopic.getTopic().getTitle())));

		supervisor.getSubordinates()
				.forEach(user -> trainingDayRepository.findByUserId(user.getId())
						.forEach(trainingDay -> {
							if (trainingDay.getScheduledDay().after(Date.valueOf(LocalDate.now()))) {
								trainingDay.getTopics().forEach(topic -> plannedTopics.add(topic.getTitle()));
							}
						}));

		return new ProgressResponse(supervisor.getName(),
				new ArrayList<>(plannedTopics),
				new ArrayList<>(learntTopics));
	}
}
