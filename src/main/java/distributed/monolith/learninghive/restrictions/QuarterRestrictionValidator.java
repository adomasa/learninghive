package distributed.monolith.learninghive.restrictions;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.List;

public class QuarterRestrictionValidator extends RestrictionValidatorDecorator {
	public QuarterRestrictionValidator(RestrictionValidator decorator) {
		super(decorator);
	}

	@Override
	public Restriction findViolatedRestriction(List<TrainingDay> existingTrainingDays,
	                                           TrainingDay newTrainingDay, List<Restriction> restrictions) {
		Restriction restriction = findRestriction(restrictions, RestrictionType.QUARTER);
		if (restriction != null) {
			LocalDate scheduledDate = newTrainingDay.getScheduledDay().toLocalDate();
			LocalDate startDate = scheduledDate.with(IsoFields.DAY_OF_QUARTER, 1).minusDays(1);
			LocalDate endDate = scheduledDate.with(IsoFields.DAY_OF_QUARTER, 1).plusMonths(3);

			if (findMatchingDays(existingTrainingDays, startDate, endDate).size() >= restriction.getDaysLimit()) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}
}
