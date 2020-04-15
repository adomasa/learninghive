package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
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

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TRAINING_DAY_QUERY)
	public @ResponseBody
	List<TrainingDayResponse> queryTrainingDays(@RequestParam(name = "userId") Long userId) {
		return trainingDayService.queryTrainingDays(userId);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = TRAINING_DAY_ADD)
	public @ResponseBody
	TrainingDayResponse addTrainingDay(@Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		return trainingDayService.addTrainingDay(trainingDayRequest);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = TRAINING_DAY_UPDATE)
	public TrainingDayResponse updateTrainingDay(@RequestParam(name = "id") Long id,
	                                             @Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		return trainingDayService.updateTrainingDay(id, trainingDayRequest);
	}

	@DeleteMapping(path = TRAINING_DAY_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTrainingDay(@RequestParam(name = "id") Long id) {
		trainingDayService.deleteTrainingDay(id);
	}
}
