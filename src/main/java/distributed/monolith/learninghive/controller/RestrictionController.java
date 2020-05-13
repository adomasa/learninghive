package distributed.monolith.learninghive.controller;


import distributed.monolith.learninghive.model.request.RestrictionRequest;
import distributed.monolith.learninghive.model.response.RestrictionResponse;
import distributed.monolith.learninghive.security.SecurityService;
import distributed.monolith.learninghive.service.RestrictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class RestrictionController {
	private final RestrictionService restrictionService;
	private final SecurityService securityService;

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = RESTRICTIONS_QUERY)
	public @ResponseBody
	List<RestrictionResponse> queryUserRestrictions(@RequestParam(name = "userId", required = false) Long userId,
	                                                @RequestParam(name = "includeGlobal", required = false) boolean includeGlobal) {
		return restrictionService.findByUserId(userId == null ?
				securityService.getLoggedUserId() : userId, includeGlobal);
	}

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(path = RESTRICTIONS_ADD)
	public @ResponseBody
	RestrictionResponse createRestriction(@Valid @RequestBody RestrictionRequest restrictionRequest) {
		return restrictionService.createRestriction(restrictionRequest);
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping(path = RESTRICTIONS_UPDATE)
	public @ResponseBody
	RestrictionResponse updateRestriction(@RequestParam(name = "id") Long id,
	                                      @Valid @RequestBody RestrictionRequest restrictionRequest) {
		return restrictionService.updateRestriction(id, restrictionRequest);
	}

	@DeleteMapping(path = RESTRICTIONS_DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRestriction(@RequestParam(name = "id") Long id) {
		restrictionService.deleteRestriction(id);
	}
}
