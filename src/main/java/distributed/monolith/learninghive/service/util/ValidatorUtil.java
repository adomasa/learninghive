package distributed.monolith.learninghive.service.util;

import distributed.monolith.learninghive.domain.VersionedEntity;
import distributed.monolith.learninghive.model.request.VersionedResourceRequest;

import javax.persistence.OptimisticLockException;

public final class ValidatorUtil {

	private ValidatorUtil() {
	}

	/**
	 * Hibernate doesn't support updating version field explicitly on managed entity.
	 * No version validation is being done in that way.
	 * Since we are using DTOs, one of workarounds is to manually check version.
	 */
	public static void validateResourceVersions(VersionedEntity entity, VersionedResourceRequest request)
			throws OptimisticLockException {
		if (!entity.getVersion().equals(request.getVersion())) {
			throw new OptimisticLockException(entity.getClass().getSimpleName()
					+ " resource has been updated by other source. Try again");
		}
	}
}
