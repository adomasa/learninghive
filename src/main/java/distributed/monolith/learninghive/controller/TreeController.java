package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.TopicTree;
import distributed.monolith.learninghive.service.TreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static distributed.monolith.learninghive.model.constants.Paths.TREE_TOPICS;

@RestController
@RequiredArgsConstructor
public class TreeController {
	private final TreeService treeService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TREE_TOPICS)
	public @ResponseBody
	TopicTree generateTopicTreeInfo() {
		return treeService.generateTopicTreeInfo();
	}
}
