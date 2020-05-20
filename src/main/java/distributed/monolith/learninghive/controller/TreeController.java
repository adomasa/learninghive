package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.TreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static distributed.monolith.learninghive.model.constants.Paths.TREE_TOPICS;

@RestController
@RequiredArgsConstructor
public class TreeController {
	private final TreeService treeService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TREE_TOPICS)
	public @ResponseBody
	TopicTree generateTopicTreeInfo(
			@RequestParam(value = "userId", required = false) Long userId) {
		return treeService.generateTopicTreeInfo(userId == null ? securityService.getLoggedUserId() : userId);
	}
}
