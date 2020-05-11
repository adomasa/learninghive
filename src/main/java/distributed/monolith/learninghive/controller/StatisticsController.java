package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.response.TeamTopicProgressResponse;
import distributed.monolith.learninghive.model.response.TeamsWithTopicResponse;
import distributed.monolith.learninghive.model.response.UsersWithTopicResponse;
import distributed.monolith.learninghive.service.StatisticsServiceImpl;
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

	private final StatisticsServiceImpl statisticsService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_WORKERS)
	public @ResponseBody
	List<UsersWithTopicResponse> findUserInfo() {
		return statisticsService.findUsersWithTopics();
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_TEAMS)
	public @ResponseBody
	List<TeamsWithTopicResponse> findTeamInfo() {
		return statisticsService.findTeamsWithTopics();
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = STATS_TEAMPROGRESS)
	public @ResponseBody
	List<TeamTopicProgressResponse> findTeamsTopicProgressInfo() {
		return statisticsService.findTeamsTopicProgress();
	}
}
