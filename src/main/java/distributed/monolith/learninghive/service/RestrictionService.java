package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.request.RestrictionRequest;
import distributed.monolith.learninghive.model.response.RestrictionResponse;

import java.util.List;

public interface RestrictionService {
	RestrictionResponse createRestriction(RestrictionRequest restrictionRequest);

	RestrictionResponse updateRestriction(Long id, RestrictionRequest restrictionRequest);

	List<RestrictionResponse> findByUserId(Long userId, boolean includeGlobal);

	void deleteRestriction(Long id);

	List<RestrictionResponse> copyToTeam(Long supervisorId, Long restrictionId);
}
