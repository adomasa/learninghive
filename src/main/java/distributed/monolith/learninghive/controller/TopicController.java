package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.LearnedTopicsResponse;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.LEARNED_TOPIC;
import static distributed.monolith.learninghive.model.constants.Paths.TOPIC;

@RestController
@RequiredArgsConstructor
public class TopicController {

	private final TopicService topicService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TOPIC)
	public @ResponseBody
	List<TopicResponse> search(@RequestParam String titlePart) {
		return topicService.searchByTitlePart(titlePart);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = TOPIC)
	public @ResponseBody
	TopicResponse addTopic(@Valid @RequestBody TopicRequest topicRequest) {
		return topicService.createTopic(topicRequest);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = TOPIC)
	public TopicResponse updateTopic(@RequestParam(name = "id") Long id,
	                                 @Valid @RequestBody TopicRequest topicRequest) {
		return topicService.updateTopic(id, topicRequest);
	}

	@DeleteMapping(path = TOPIC)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTopic(@RequestParam(name = "id") Long id) {
		topicService.delete(id);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = LEARNED_TOPIC)
	public void createLearnedTopic(@RequestParam("id") Long id) {
		topicService.createLearnedTopic(id, securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(path = LEARNED_TOPIC)
	public void deleteLearnedTopic(@RequestParam("id") Long id) {
		topicService.deleteLearnedTopic(id, securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = LEARNED_TOPIC)
	public LearnedTopicsResponse getLearnedTopics(
			@RequestParam(value = "userId", required = false) Long userId) {
		return topicService.findLearnedTopics(userId == null ? securityService.getLoggedUserId() : userId);
	}
}
