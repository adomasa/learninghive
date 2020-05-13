package distributed.monolith.learninghive.restrictions;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RestrictionValidatorDecorator extends RestrictionValidator {
	protected RestrictionValidator decorator;

	public RestrictionValidatorDecorator(RestrictionValidator decorator) {
		this.decorator = decorator;
	}

	protected Restriction findRestriction(List<Restriction> restrictions, RestrictionType restrictionType) {
		return restrictions.stream()
				.filter(r -> r.getRestrictionType() == restrictionType)
				.findFirst()
				.orElse(null);
	}

	protected List<TrainingDay> findMatchingDays(List<TrainingDay> trainingDays, LocalDate startDate,
	                                             LocalDate endDate) {
		return trainingDays.stream()
				.filter(t -> t.getScheduledDay().toLocalDate().isAfter(startDate)
						&& t.getScheduledDay().toLocalDate().isBefore(endDate))
				.collect(Collectors.toList());
	}
}
