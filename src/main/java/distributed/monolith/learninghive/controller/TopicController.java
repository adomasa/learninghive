package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.TopicRequest;
import distributed.monolith.learninghive.model.response.TopicResponse;
import distributed.monolith.learninghive.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class TopicController {

	private final TopicService topicService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TOPIC_QUERY)
	public @ResponseBody
	List<TopicResponse> search(@RequestParam String titlePart) {
		return topicService.searchByTitlePart(titlePart);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = TOPIC_ADD)
	public @ResponseBody
	TopicResponse addTopic(@Valid @RequestBody TopicRequest topicRequest) {
		return new TopicResponse(topicService.createTopic(topicRequest));
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = TOPIC_UPDATE)
	public void updateTopic(@RequestParam(name = "id") Long id,
	                        @Valid @RequestBody TopicRequest topicRequest) {
		topicService.updateTopic(id, topicRequest);
	}

	@DeleteMapping(path = TOPIC_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTopic(@RequestParam(name = "id") Long id) {
		topicService.delete(id);
	}
}
