package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.TrainingDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class TrainingDayController {
	private final TrainingDayService trainingDayService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TRAINING_DAY_QUERY)
	public @ResponseBody
	List<TrainingDayResponse> queryTrainingDays(@RequestParam(name = "userId", required = false) Long userId) {
		return trainingDayService.queryTrainingDays(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = TRAINING_DAY_ADD)
	public @ResponseBody
	TrainingDayResponse addTrainingDay(@Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		setUserId(trainingDayRequest);
		return trainingDayService.addTrainingDay(trainingDayRequest);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = TRAINING_DAY_UPDATE)
	public TrainingDayResponse updateTrainingDay(@RequestParam(name = "id") Long id,
	                                             @Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		setUserId(trainingDayRequest);
		return trainingDayService.updateTrainingDay(id, trainingDayRequest);
	}

	@DeleteMapping(path = TRAINING_DAY_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTrainingDay(@RequestParam(name = "id") Long id) {
		trainingDayService.deleteTrainingDay(id);
	}

	private void setUserId(TrainingDayRequest request) {
		if(request.getUserId() == null) {
			request.setUserId(securityService.getLoggedUserId());
		}
	}
}
