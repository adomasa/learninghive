package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_EMPLOYEES)
	public @ResponseBody
	UsersWithTopicResponse findUsersWithTopics(@RequestParam(name = "topicId") Long topicId) {
		return statisticsService.findUsersWithTopics(topicId, securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_SUBORDINATES)
	public @ResponseBody
	SubordinatesWithSubCount countSubordinatesWithTopics(@RequestParam(name = "topicId") Long topicId) {
		return statisticsService.countSubordinatesWithTopics(topicId, securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_SUBPROGRESS)
	public @ResponseBody
	ProgressResponse findSubordinatesProgress() {
		return statisticsService.findSubordinatesProgress(securityService.getLoggedUserId());
	}
}