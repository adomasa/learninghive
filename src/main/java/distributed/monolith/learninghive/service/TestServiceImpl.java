package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final UserRepository userRepository;
	private final InvitationRepository invitationRepository;
	private final LearnedTopicRepository learnedTopicRepository;
	private final ObjectiveRepository objectiveRepository;
	private final TopicService topicService;
	private final TopicRepository topicRepository;
	List<Topic> topicList = new ArrayList<>();

	@Override
	public void addTestData() {
		TopicRequest topicRequest = new TopicRequest();
		topicRequest.setTitle("Java");
		topicRequest.setContent("asd");
		TopicResponse topicResponse = topicService.createTopic(topicRequest);

		TopicRequest topicRequest2 = new TopicRequest();
		topicRequest2.setTitle("Spring");
		topicRequest2.setContent("asd");
		topicRequest2.setParentId(topicResponse.getId());
		topicService.createTopic(topicRequest2);

		//topicRequest.set
//		topicList.add(new Topic("Java"));
//		topicList.add(new Topic("SpringBoot"));
//		topicList.add(new Topic("JPA"));
//		topicList.add(new Topic("Docker"));
//		topicList.add(new Topic("Kubernetes"));
//		topicList.add(new Topic("AWS"));
//		topicList.add(new Topic("ECS"));
//		topicList.add(new Topic("Pipeline"));
//		topicList.add(new Topic("Docker compose"));
//		topicList.add(new Topic("Networking"));
//		topicList.add(new Topic("Volumes"));
////		topicList.get(0).setChildren(List.of(topicList.get(1), topicList.get(2)));
//		topicList.get(1).setParent(topicList.get(0));
//		topicList.get(2).setParent(topicList.get(0));
//		//topicList.get(3).setChildren(List.of(topicList.get(4), topicList.get(5), topicList.get(8)));
////		topicList.get(4).setParent(topicList.get(3));
////		topicList.get(5).setParent(topicList.get(3));
////		topicList.get(8).setParent(topicList.get(3));
////		//topicList.get(5).setChildren(List.of(topicList.get(6), topicList.get(7)));
////		topicList.get(6).setParent(topicList.get(5));
////		topicList.get(7).setParent(topicList.get(5));
////		//topicList.get(8).setChildren(List.of(topicList.get(9), topicList.get(10)));
////		topicList.get(9).setParent(topicList.get(8));
////		topicList.get(10).setParent(topicList.get(8));
//
//		for (Topic topic : topicList) {
//			TopicRequest topicRequest = new TopicRequest();
//			topicRequest.setTitle(topic.getTitle());
//			if (!topic.getContent().isEmpty()) topicRequest.setContent(topic.getContent());
//			if (topic.getParent() != null) topicRequest.setParentId(topic.getParent().getId());
////			List<Long> childrenIds = new ArrayList<>();
////			for (Topic child : topic.getChildren()) {
////				childrenIds.add(child.getId());
////			}
////			topicRequest.setChildrenId(childrenIds);
////			childrenIds.clear();
//			topicService.createTopic(topicRequest);
//		}
	}
}
