package distributed.monolith.learninghive.model.exception;

import distributed.monolith.learninghive.domain.Restriction;

public class RestrictionViolationException extends RuntimeException {
	private static final long serialVersionUID = 6323247181384781046L;

	public RestrictionViolationException(Restriction restriction) {
		super(String.format("Restriction with id %d, type %s and day limit %d violated",
				restriction.getId(), restriction.getRestrictionType().toString(), restriction.getDaysLimit()));
	}
}
