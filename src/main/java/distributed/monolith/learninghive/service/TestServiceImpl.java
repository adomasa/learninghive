package distributed.monolith.learninghive.service;


import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final UserRepository userRepository;
	private final InvitationRepository invitationRepository;
	private final LearnedTopicRepository learnedTopicRepository;
	private final ObjectiveRepository objectiveRepository;
	private final TopicService topicService;
	private final TopicRepository topicRepository;

	private TopicResponse addNewTopicWithParent(String title, TopicResponse topicsParent) {
		TopicRequest topicRequest = new TopicRequest();
		topicRequest.setTitle(title);
		topicRequest.setParentId(topicsParent.getId());
		return topicService.createTopic(topicRequest);
	}

	@Override
	public void addTestData() {
		TopicRequest javaTopicRequest = new TopicRequest();
		javaTopicRequest.setTitle("Java");
		TopicResponse javaTopicResponse = topicService.createTopic(javaTopicRequest);

		TopicRequest DockerRequest = new TopicRequest();
		DockerRequest.setTitle("Docker");
		TopicResponse dockerResponse = topicService.createTopic(DockerRequest);

		TopicResponse springBootResponse = addNewTopicWithParent("SpringBoot", javaTopicResponse);
		TopicResponse jpaResponse = addNewTopicWithParent("JPA", javaTopicResponse);
		TopicResponse kubernetesResponse = addNewTopicWithParent("Kubernetes", dockerResponse);
		TopicResponse awsResponse = addNewTopicWithParent("AWS", dockerResponse);
		TopicResponse dockerComposeResponse = addNewTopicWithParent("Docker compose", dockerResponse);
		TopicResponse ecsResponse = addNewTopicWithParent("ECS", awsResponse);
		TopicResponse pipelineResponse = addNewTopicWithParent("Pipeline", awsResponse);
		TopicResponse networkingResponse = addNewTopicWithParent("Networking", dockerComposeResponse);
		TopicResponse volumesResponse = addNewTopicWithParent("Volumes", dockerComposeResponse);
	}
}
