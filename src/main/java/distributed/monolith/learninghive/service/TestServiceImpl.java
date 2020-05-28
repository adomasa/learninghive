package distributed.monolith.learninghive.service;


import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.Topic;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final UserService userService;
	private final UserRepository userRepository;
	private final LearnedTopicService learnedTopicService;
	private final ObjectiveService objectiveService;
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

		TopicRequest dockerRequest = new TopicRequest();
		dockerRequest.setTitle("Docker");
		TopicResponse dockerResponse = topicService.createTopic(dockerRequest);

		addNewTopicWithParent("SpringBoot", javaTopicResponse);
		addNewTopicWithParent("JPA", javaTopicResponse);
		addNewTopicWithParent("Kubernetes", dockerResponse);
		TopicResponse awsResponse = addNewTopicWithParent("AWS", dockerResponse);
		TopicResponse dockerComposeResponse = addNewTopicWithParent("Docker compose", dockerResponse);
		addNewTopicWithParent("ECS", awsResponse);
		addNewTopicWithParent("Pipeline", awsResponse);
		addNewTopicWithParent("Networking", dockerComposeResponse);
		addNewTopicWithParent("Volumes", dockerComposeResponse);
	}

	@Override
	public void addTestUsers(Long loggedUserId) {
		InvitationData petrasResponse = addNewInvitation(loggedUserId, "petras@petras.com");
		InvitationData juozasResponse = addNewInvitation(loggedUserId, "juozas@juozas.com");
		InvitationData juliusResponse = addNewInvitation(loggedUserId, "julius@julius.com");
		User petrasRegResponse = registerNewUser
				(petrasResponse, "PetrasPetras123", "Petras", "Petraitis");
		registerNewUser
				(juozasResponse, "JuozasJuozas123", "Juozas", "Juozaitis");
		User juliusRegResponse = registerNewUser
				(juliusResponse, "JuliusJulius123", "Julius", "Julaitis");

		InvitationData ervinasResponse = addNewInvitation(petrasRegResponse.getId(), "ervinas@ervinas.com");
		InvitationData eisvydasResponse = addNewInvitation(petrasRegResponse.getId(), "eisvydas@eisvydas.com");
		InvitationData vaidotasResponse = addNewInvitation(petrasRegResponse.getId(), "vaidotas@vaidotas.com");
		InvitationData vilteResponse = addNewInvitation(petrasRegResponse.getId(), "vilte@vilte.com");
		registerNewUser
				(ervinasResponse, "ErvinasErvinas123", "Ervinas", "Ervinaitis");
		registerNewUser
				(eisvydasResponse, "EisvydasEisvydas123", "Eisvydas", "Eisvydaitis");
		registerNewUser
				(vaidotasResponse, "VaidotasVaidotas123", "Vaidotas", "Vaidotaitis");
		registerNewUser
				(vilteResponse, "VilteVilteErvinas123", "Vilte", "Viltaite");

		InvitationData smilteResponse = addNewInvitation(juliusRegResponse.getId(), "smilte@smilte.com");
		User smilteRegResponse =
				registerNewUser(smilteResponse, "SmilteSmilte123", "Smilte", "Smiltaite");

		InvitationData sauleResponse = addNewInvitation(smilteRegResponse.getId(), "saule@saule.com");
		InvitationData vytauteResponse = addNewInvitation(smilteRegResponse.getId(), "vytaute@vytaute.com");
		InvitationData ainiusResponse = addNewInvitation(smilteRegResponse.getId(), "ainius@ainius.com");
		registerNewUser
				(sauleResponse, "SauleSaule123", "Saule", "Saulaite");
		registerNewUser
				(vytauteResponse, "VytauteVytaute123", "Vytaute", "Vytautaite");
		registerNewUser
				(ainiusResponse, "AiniusAinius123", "Ainius", "Ainaitis");
	}

	@Override
	public void addTestTopicsToUsers() {
		List<Long> topicList = topicRepository.findAll().stream().map(Topic::getId).collect(Collectors.toList());
		List<Long> userList = userRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
		Random rand = new Random();
		for (int index = 0; index < 30; index++) {
			learnedTopicService.createLearnedTopic
					(topicList.get(rand.nextInt(topicList.size())), userList.get(rand.nextInt(userList.size())));
		}

		List<ObjectiveRequest> objectiveRequests = new ArrayList<>();
		for (int index = 0; index < 15; index++) {
			ObjectiveRequest objectiveRequest = new ObjectiveRequest();
			objectiveRequest.setTopicId(topicList.get(rand.nextInt(topicList.size())));
			objectiveRequest.setUserId(userList.get(rand.nextInt(userList.size())));

			if (objectiveRequests.contains(objectiveRequest)) {
				continue;
			}
			objectiveRequests.add(objectiveRequest);
			objectiveService.addObjective(objectiveRequest);
		}
	}

	private User registerNewUser(InvitationData userResponse, String pass, String name, String lastName) {
		UserRegistration userRegistration = new UserRegistration(pass, name, lastName);
		return userService.registerUser(userResponse.getLink()
				.substring(userResponse.getLink().lastIndexOf("=") + 1), userRegistration, Role.EMPLOYEE);
	}

	private InvitationData addNewInvitation(Long loggedUserId, String email) {
		UserInvitation userInvitation = new UserInvitation();
		userInvitation.setEmail(email);
		return userService.createInvitation(userInvitation, loggedUserId);
	}
}
