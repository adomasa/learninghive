package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@Service
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {
	private final TopicRepository topicRepository;

	@Override
	public TopicTree generateTopicTreeInfo() {
		List<Topic> topicsTopLevel = findTopicsWithNoParents();
		TopicTree topicTreeRoot = new TopicTree();
		topicTreeRoot.setName("Topics");

		for (Topic topic : topicsTopLevel) {
			TopicTree topicTreeNode = new TopicTree();
			topicTreeNode.setName(topic.getTitle());
			topicTreeNode.setId(topic.getId());

			topicTreeNode.setChildren(getNodesChildrenInfo(topic));

			topicTreeRoot.addToList(topicTreeNode);
		}
		return topicTreeRoot;
	}

	private List<TopicTree> getNodesChildrenInfo(Topic topic) {
		List<TopicTree> topicTrees = new ArrayList<>();
		for (Topic topicChild : topic.getChildren()) {
			TopicTree topicChildTree = new TopicTree();
			topicChildTree.setName(topicChild.getTitle());
			topicChildTree.setId(topicChild.getId());
			topicChildTree.setChildren(getNodesChildrenInfo(topicChild));
			topicTrees.add(topicChildTree);
		}
		return topicTrees;
	}


	private List<Topic> findTopicsWithNoParents() {
		return topicRepository.findAll()
				.stream()
				.filter(topic -> topic.getParent() == null)
				.collect(Collectors.toList());
	}
}
