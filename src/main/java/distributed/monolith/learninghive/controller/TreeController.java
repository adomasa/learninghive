package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.TreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static distributed.monolith.learninghive.model.constants.Paths.TREE_SUBORDINATES;
import static distributed.monolith.learninghive.model.constants.Paths.TREE_USER;

@RestController
@RequiredArgsConstructor
public class TreeController {
	private final TreeService treeService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TREE_USER)
	public @ResponseBody
	TopicTree generateUserTree(
			@RequestParam(value = "userId", required = false) Long userId) {
		return treeService.generateUserTree(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TREE_SUBORDINATES)
	public @ResponseBody
	TopicTree generateSubordinatesTree(
			@RequestParam(value = "userId", required = false) Long userId) {
		return treeService.generateSubordinateTree(userId == null ? securityService.getLoggedUserId() : userId);
	}
}
