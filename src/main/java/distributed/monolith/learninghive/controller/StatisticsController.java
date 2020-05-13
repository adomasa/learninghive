package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.TeamTopicProgressResponse;
import distributed.monolith.learninghive.model.response.TeamsWithTopicResponse;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
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

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_EMPLOYEES)
	public @ResponseBody
	List<UsersWithTopicResponse> findUsersWithTopics() {
		return statisticsService.findUsersWithTopics();
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_SUBORDINATES)
	public @ResponseBody
	List<TeamsWithTopicResponse> countSubordinatesWithTopics() {
		return statisticsService.countSubordinatesWithTopics();
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_TEAMPROGRESS)
	public @ResponseBody
	List<TeamTopicProgressResponse> findTeamsTopicProgressInfo() {
		return statisticsService.findTeamsTopicProgress();
	}
}
