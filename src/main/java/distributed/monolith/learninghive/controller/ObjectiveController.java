package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.response.ObjectiveResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.OBJECTIVE;

@RestController
@RequiredArgsConstructor
public class ObjectiveController {

	private final ObjectiveService objectiveService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = OBJECTIVE)
	public @ResponseBody
	List<ObjectiveResponse> findUserObjectives(@RequestParam(name = "userId", required = false) Long userId) {
		return objectiveService.findByUserId(userId == null ? securityService.getLoggedUserId() : userId);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = OBJECTIVE)
	public @ResponseBody
	ObjectiveResponse addObjective(@Valid @RequestBody ObjectiveRequest objectiveRequest) {
		setUserId(objectiveRequest);
		return objectiveService.addObjective(objectiveRequest);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = OBJECTIVE)
	public ObjectiveResponse updateObjective(@RequestParam(name = "id") Long id,
	                                         @Valid @RequestBody ObjectiveRequest objectiveRequest) {
		setUserId(objectiveRequest);
		return objectiveService.updateObjective(id, objectiveRequest);
	}

	@DeleteMapping(path = OBJECTIVE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteObjective(@RequestParam(name = "id") Long id) {
		objectiveService.deleteObjective(id);
	}

	private void setUserId(ObjectiveRequest request) {
		if (request.getUserId() == null) {
			request.setUserId(securityService.getLoggedUserId());
		}
	}
}
