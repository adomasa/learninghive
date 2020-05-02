package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Objective;
import distributed.monolith.learninghive.model.request.ObjectiveRequest;
import distributed.monolith.learninghive.model.response.ObjectiveResponse;

import java.util.List;

public interface ObjectiveService {
	ObjectiveResponse updateObjective(long id, ObjectiveRequest objectiveRequest);

	ObjectiveResponse addObjective(ObjectiveRequest objectiveRequest);

	void deleteObjective(long id);

	List<ObjectiveResponse> findByUserId(long userId);

	void mountEntity(Objective destination, ObjectiveRequest source);
}
