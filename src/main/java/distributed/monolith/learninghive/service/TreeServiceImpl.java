package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.LearnedTopic;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.repository.LearnedTopicRepository;
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
public class TreeServiceImpl implements TreeService {
	private final LearnedTopicRepository learnedTopicRepository;

	@Override
	public TopicTree generateTopicTreeInfo(long userId) {
		//Get topics that are already learned by the user
		Set<Topic> learntTopics = learnedTopicRepository.findByUserId(userId)
				.stream()
				.map(LearnedTopic::getTopic)
				.collect(Collectors.toSet());

		//Get learned topics with parents
		Set<Topic> learntTopicsWithParents = new HashSet<>(learntTopics);
		for (Topic learntTopic : learntTopics) {
			addNodesParents(learntTopic, learntTopicsWithParents);
		}

		//Find top nodes with no parents
		Set<Topic> topNodes = findTopicsWithNoParents(learntTopicsWithParents);

		//Create root node
		TopicTree topicTreeRoot = new TopicTree();
		topicTreeRoot.setName("Topics");

		//Populate root node
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
