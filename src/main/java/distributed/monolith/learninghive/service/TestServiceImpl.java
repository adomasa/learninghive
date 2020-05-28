package distributed.monolith.learninghive.service;


import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.TopicResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final UserService userService;
	private final LearnedTopicService learnedTopicService;
	private final ObjectiveService objectiveService;
	private final TopicService topicService;
	private List<Long> topicIds = new ArrayList<>();
	private List<Long> userIds = new ArrayList<>();

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
		topicIds.add(javaTopicResponse.getId());
		TopicRequest dockerRequest = new TopicRequest();
		dockerRequest.setTitle("Docker");
		TopicResponse dockerResponse = topicService.createTopic(dockerRequest);
		topicIds.add(dockerResponse.getId());

		topicIds.add(addNewTopicWithParent("SpringBoot", javaTopicResponse).getId());
		topicIds.add(addNewTopicWithParent("JPA", javaTopicResponse).getId());
		topicIds.add(addNewTopicWithParent("Kubernetes", dockerResponse).getId());

		TopicResponse awsResponse = addNewTopicWithParent("AWS", dockerResponse);
		TopicResponse dockerComposeResponse = addNewTopicWithParent("Docker compose", dockerResponse);
		topicIds.add(awsResponse.getId());
		topicIds.add(dockerComposeResponse.getId());

		topicIds.add(addNewTopicWithParent("ECS", awsResponse).getId());
		topicIds.add(addNewTopicWithParent("Pipeline", awsResponse).getId());
		topicIds.add(addNewTopicWithParent("Networking", dockerComposeResponse).getId());
		topicIds.add(addNewTopicWithParent("Volumes", dockerComposeResponse).getId());
	}

	@Override
	public void addTestUsers(Long loggedUserId) {
		userIds.add(loggedUserId);
		InvitationData petrasResponse = addNewInvitation(loggedUserId, "petras@petras.com");
		InvitationData juozasResponse = addNewInvitation(loggedUserId, "juozas@juozas.com");
		InvitationData juliusResponse = addNewInvitation(loggedUserId, "julius@julius.com");
		User petrasRegResponse = registerNewUser
				(petrasResponse, "PetrasPetras123", "Petras", "Petraitis");
		userIds.add(registerNewUser
				(juozasResponse, "JuozasJuozas123", "Juozas", "Juozaitis").getId());
		User juliusRegResponse = registerNewUser
				(juliusResponse, "JuliusJulius123", "Julius", "Julaitis");
		userIds.add(petrasRegResponse.getId());
		userIds.add(juliusRegResponse.getId());

		InvitationData ervinasResponse = addNewInvitation(petrasRegResponse.getId(), "ervinas@ervinas.com");
		InvitationData eisvydasResponse = addNewInvitation(petrasRegResponse.getId(), "eisvydas@eisvydas.com");
		InvitationData vaidotasResponse = addNewInvitation(petrasRegResponse.getId(), "vaidotas@vaidotas.com");
		InvitationData vilteResponse = addNewInvitation(petrasRegResponse.getId(), "vilte@vilte.com");
		userIds.add(registerNewUser
				(ervinasResponse, "ErvinasErvinas123", "Ervinas", "Ervinaitis").getId());
		userIds.add(registerNewUser
				(eisvydasResponse, "EisvydasEisvydas123", "Eisvydas", "Eisvydaitis").getId());
		userIds.add(registerNewUser
				(vaidotasResponse, "VaidotasVaidotas123", "Vaidotas", "Vaidotaitis").getId());
		userIds.add(registerNewUser
				(vilteResponse, "VilteVilteErvinas123", "Vilte", "Viltaite").getId());

		InvitationData smilteResponse = addNewInvitation(juliusRegResponse.getId(), "smilte@smilte.com");
		User smilteRegResponse =
				registerNewUser(smilteResponse, "SmilteSmilte123", "Smilte", "Smiltaite");
		userIds.add(smilteRegResponse.getId());

		InvitationData sauleResponse = addNewInvitation(smilteRegResponse.getId(), "saule@saule.com");
		InvitationData vytauteResponse = addNewInvitation(smilteRegResponse.getId(), "vytaute@vytaute.com");
		InvitationData ainiusResponse = addNewInvitation(smilteRegResponse.getId(), "ainius@ainius.com");
		userIds.add(registerNewUser
				(sauleResponse, "SauleSaule123", "Saule", "Saulaite").getId());
		userIds.add(registerNewUser
				(vytauteResponse, "VytauteVytaute123", "Vytaute", "Vytautaite").getId());
		userIds.add(registerNewUser
				(ainiusResponse, "AiniusAinius123", "Ainius", "Ainaitis").getId());
	}

	@Override
	public void addTestTopicsToUsers(int randomTopicCount, int randomObjectiveCount) {
		Random rand = new Random();
		for (int index = 0; index < randomTopicCount; index++) {
			learnedTopicService.createLearnedTopic
					(topicIds.get(rand.nextInt(topicIds.size())), userIds.get(rand.nextInt(userIds.size())));
		}

		List<ObjectiveRequest> objectiveRequests = new ArrayList<>();
		for (int index = 0; index < randomObjectiveCount; index++) {
			ObjectiveRequest objectiveRequest = new ObjectiveRequest();
			objectiveRequest.setTopicId(topicIds.get(rand.nextInt(topicIds.size())));
			objectiveRequest.setUserId(userIds.get(rand.nextInt(userIds.size())));

			if (objectiveRequests.contains(objectiveRequest)) {
				continue;
			}
			objectiveRequests.add(objectiveRequest);
			objectiveService.addObjective(objectiveRequest);
		}
	}

	private User registerNewUser(InvitationData userResponse, String pass, String name, String lastName) {
		UserRegistration userRegistration = new UserRegistration(pass, name, lastName);

		return userService.registerUser(UriComponentsBuilder
				.fromUriString(userResponse.getLink())
				.build()
				.getQueryParams()
				.get("token")
				.get(0), userRegistration, Role.EMPLOYEE);
	}


	private InvitationData addNewInvitation(Long loggedUserId, String email) {
		UserInvitation userInvitation = new UserInvitation();
		userInvitation.setEmail(email);
		return userService.createInvitation(userInvitation, loggedUserId);
	}
}
