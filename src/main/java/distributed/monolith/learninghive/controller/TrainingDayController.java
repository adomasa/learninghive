package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.exception.RestrictionViolationException;
import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.AuthorityService;
import distributed.monolith.learninghive.service.TrainingDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.TRAINING_DAY;

@RestController
@RequiredArgsConstructor
public class TrainingDayController {

	private final TrainingDayService trainingDayService;
	private final SecurityService securityService;
	private final AuthorityService authorityService;

	private static int restrictionViolatedStatus = 432;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = TRAINING_DAY)
	public @ResponseBody
	List<TrainingDayResponse> queryTrainingDays(@RequestParam(name = "userId", required = false) Long userId) {
		authorityService.validateLoggedUserOrSupervisor(userId);
		return trainingDayService.queryTrainingDays(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@PostMapping(path = TRAINING_DAY)
	public @ResponseBody
	ResponseEntity addTrainingDay(@Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		authorityService.validateLoggedUserOrSupervisor(trainingDayRequest.getUserId());
		setUserId(trainingDayRequest);
		ResponseEntity response;
		try {
			response = new ResponseEntity(trainingDayService.addTrainingDay(trainingDayRequest), HttpStatus.OK);
		} catch (RestrictionViolationException ex) {
			response = ResponseEntity.status(restrictionViolatedStatus).body(ex.getMessage());
		}
		return response;
	}

	@PutMapping(path = TRAINING_DAY)
	public ResponseEntity updateTrainingDay(@RequestParam(name = "id") Long id,
	                                             @Valid @RequestBody TrainingDayRequest trainingDayRequest) {
		authorityService.validateLoggedUserOrSupervisor(trainingDayRequest.getUserId());
		setUserId(trainingDayRequest);
		ResponseEntity response;
		try {
			response = new ResponseEntity(trainingDayService.updateTrainingDay(id, trainingDayRequest),
					HttpStatus.OK);
		} catch (RestrictionViolationException ex) {
			response = ResponseEntity.status(restrictionViolatedStatus).body(ex.getMessage());
		}
		return response;
	}

	@DeleteMapping(path = TRAINING_DAY)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTrainingDay(@RequestParam(name = "id") Long id) {
		trainingDayService.deleteTrainingDay(id);
	}

	private void setUserId(TrainingDayRequest request) {
		if (request.getUserId() == null) {
			request.setUserId(securityService.getLoggedUserId());
		}
	}
}
