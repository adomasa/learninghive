package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.response.TeamTopicProgressResponse;
import distributed.monolith.learninghive.model.response.TeamResponse;
import distributed.monolith.learninghive.model.response.TeamsWithTopicResponse;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.repository.ObjectiveRepository;
import distributed.monolith.learninghive.repository.TopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private final TopicRepository topicRepository;
	private final ObjectiveRepository objectiveRepository;
	private final UserRepository userRepository;

	@Override
	public List<UsersWithTopicResponse> findUsersWithTopics() {
		List<Topic> allTopics = topicRepository.findAll();
		List<String> namesOfWorkers = new ArrayList<>();
		List<UsersWithTopicResponse> usersWithTopicResponses = new ArrayList<>();

		for (Topic topic : allTopics) {
			List<Objective> allObjectives = objectiveRepository.findByTopicId(topic.getId());

			for (Objective objective : allObjectives) {
				if (objective.getCompleted()) {
					namesOfWorkers.add(objective.getUser().getName() + " " + objective.getUser().getSurname());
				}
			}

			usersWithTopicResponses.add(new UsersWithTopicResponse(topic.getTitle(), namesOfWorkers));
			namesOfWorkers.clear();
		}

		return usersWithTopicResponses;
	}

	@Override
	public List<TeamsWithTopicResponse> findTeamsWithTopics() {
		List<Topic> allTopics = topicRepository.findAll();
		List<User> allLeaders = getAllLeaders();
		List<TeamsWithTopicResponse> teamsWithTopicResponses = new ArrayList<>();

		int counter = 0;
		List<TeamResponse> teamResponses = new ArrayList<>();
		for (Topic topic : allTopics) {
			for (User leader : allLeaders) {
				List<User> allSubordinates = leader.getSubordinates();

				for (User subordinate : allSubordinates) {
					Objective objective = objectiveRepository.findByUserIdAndTopicId(subordinate.getId(), topic.getId());
					if (objective != null && objective.getCompleted()) {
						counter++;
					}
				}
				teamResponses.add(new TeamResponse(leader.getName(), counter, allSubordinates.size()));
				counter = 0;
			}
			teamsWithTopicResponses.add(new TeamsWithTopicResponse(topic.getTitle(), teamResponses));
			teamResponses.clear();
		}

		return teamsWithTopicResponses;
	}

	@Override
	public List<TeamTopicProgressResponse> findTeamsTopicProgress() {
		List<User> allLeaders = getAllLeaders();
		List<Topic> allTopics = topicRepository.findAll();
		List<String> learntTopics = new ArrayList<>();
		List<String> plannedTopics = new ArrayList<>();
		List<TeamTopicProgressResponse> topicProgressResponses = new ArrayList<>();
		for (User leader : allLeaders) {
			for (Topic topic : allTopics) {
				List<User> allSubordinates = leader.getSubordinates();
				for (User subordinate : allSubordinates) {
					Objective objective = objectiveRepository.findByUserIdAndTopicId(subordinate.getId(), topic.getId());
					if (objective != null) {
						if (objective.getCompleted()) {
							if (!learntTopics.contains(objective.getTopic().getTitle())) {
								learntTopics.add(objective.getTopic().getTitle());
							}
						} else {
							if (!plannedTopics.contains(objective.getTopic().getTitle())) {
								plannedTopics.add(objective.getTopic().getTitle());
							}
						}
					}
				}
			}
			topicProgressResponses.add
					(new TeamTopicProgressResponse(leader.getName(), plannedTopics, learntTopics));
			plannedTopics.clear();
			learntTopics.clear();
		}
		return topicProgressResponses;
	}

	private List<User> getAllLeaders() {
		List<User> allUsers = (List<User>) userRepository.findAll();
		List<User> allLeaders = new ArrayList<>();
		for (User user : allUsers) {
			if (!user.getSubordinates().isEmpty()) {
				allLeaders.add(user);
			}
		}
		return allLeaders;
	}
}
