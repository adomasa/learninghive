package distributed.monolith.learninghive.controller;

import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.response.ObjectiveResponse;
import distributed.monolith.learninghive.service.ObjectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static distributed.monolith.learninghive.model.constants.Paths.*;

@RestController
@RequiredArgsConstructor
public class ObjectiveController {
    private final ObjectiveService objectiveService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = OBJECTIVE_QUERY)
    public @ResponseBody
    List<ObjectiveResponse> queryUserObjectives(@RequestParam(name = "userId") Long userId) {
        return objectiveService.searchByUserId(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = OBJECTIVE_ADD)
    public @ResponseBody
    ObjectiveResponse addObjective(@Valid @RequestBody ObjectiveRequest objectiveRequest) {
        return objectiveService.addObjective(objectiveRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = OBJECTIVE_UPDATE)
    public ObjectiveResponse updateObjective(@RequestParam(name = "id") Long id,
                                     @Valid @RequestBody ObjectiveRequest objectiveRequest) {
        return objectiveService.updateObjective(id, objectiveRequest);
    }

    @DeleteMapping(path = OBJECTIVE_DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteObjective(@RequestParam(name = "id") Long id) {
        objectiveService.deleteObjective(id);
    }
}
