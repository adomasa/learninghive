package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.LearnedTopic;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.exception.ResourceNotFoundException;
import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
import distributed.monolith.learninghive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@Service
@RequiredArgsConstructor
public class LearningTreeServiceImpl implements LearningTreeService {
	private final LearnedTopicRepository learnedTopicRepository;
	private final UserRepository userRepository;

	@Override
	public TopicTree generateUserTree(long userId) {
		Set<Topic> learntTopics = learnedTopicRepository.findByUserId(userId)
				.stream()
				.map(LearnedTopic::getTopic)
				.collect(Collectors.toSet());

		return getTopicTree(learntTopics);
	}

	@Override
	public TopicTree generateSubordinateTree(long userId) {
		var user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						User.class,
						userId
				));

		Set<Topic> learntTopics = new HashSet<>();
		for (User subordinate : user.getSubordinates()) {
			learntTopics.addAll(learnedTopicRepository.findByUserId(subordinate.getId())
					.stream()
					.map(LearnedTopic::getTopic)
					.collect(Collectors.toSet()));
		}

		return getTopicTree(learntTopics);
	}

	private TopicTree getTopicTree(Set<Topic> learntTopics) {
		Set<Topic> learntTopicsWithParents = new HashSet<>(learntTopics);

		for (Topic learntTopic : learntTopics) {
			addNodesParents(learntTopic, learntTopicsWithParents);
		}

		Set<Topic> topNodes = findTopicsWithNoParents(learntTopicsWithParents);

		TopicTree topicTreeRoot = new TopicTree();
		topicTreeRoot.setName("Topics");

		for (Topic topic : topNodes) {
			TopicTree topicTreeNode = new TopicTree();
			topicTreeNode.setName(topic.getTitle());
			topicTreeNode.setId(topic.getId());

			topicTreeNode.setChildren(getNodesChildrenInfo(topic, learntTopicsWithParents));
			topicTreeRoot.addToList(topicTreeNode);
		}

		return topicTreeRoot;
	}

	private void addNodesParents(Topic topic, Set<Topic> learntTopics) {
		if (topic.getParent() != null) {
			learntTopics.add(topic.getParent());
			addNodesParents(topic.getParent(), learntTopics);
		}
	}

	private List<TopicTree> getNodesChildrenInfo(Topic topic, Set<Topic> learntTopicsWithParents) {
		List<TopicTree> topicTrees = new ArrayList<>();

		for (Topic topicChild : topic.getChildren()) {
			if (learntTopicsWithParents.contains(topicChild)) {
				TopicTree topicChildTree = new TopicTree();
				topicChildTree.setName(topicChild.getTitle());
				topicChildTree.setId(topicChild.getId());
				topicChildTree.setChildren(getNodesChildrenInfo(topicChild, learntTopicsWithParents));
				topicTrees.add(topicChildTree);
			}
		}

		return topicTrees;
	}

	private Set<Topic> findTopicsWithNoParents(Set<Topic> learntTopicsWithParents) {
		return learntTopicsWithParents.stream()
				.filter(topic -> topic.getParent() == null)
				.collect(Collectors.toSet());
	}
}
