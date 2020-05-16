package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.ProgressResponse;
import distributed.monolith.learninghive.model.response.SubordinatesWithSubCount;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

	private final StatisticsService statisticsService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_EMPLOYEES)
	public @ResponseBody
	List<UsersWithTopicResponse> findUsersWithTopics() {
		return statisticsService.findUsersWithTopics(securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_SUBORDINATES)
	public @ResponseBody
	List<SubordinatesWithSubCount> countSubordinatesWithTopics() {
		return statisticsService.countSubordinatesWithTopics(securityService.getLoggedUserId());
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_PROGRESS)
	public @ResponseBody
	ProgressResponse findSubordinatesProgress() {
		return statisticsService.findSubordinatesProgress(securityService.getLoggedUserId());
	}
}
