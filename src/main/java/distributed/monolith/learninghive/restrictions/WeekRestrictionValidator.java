package distributed.monolith.learninghive.restrictions;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

public class WeekRestrictionValidator extends RestrictionValidatorDecorator {
	public WeekRestrictionValidator(RestrictionValidator decorator) {
		super(decorator);
	}

	@Override
	public Restriction findViolatedRestriction(List<TrainingDay> existingTrainingDays,
	                                           TrainingDay newTrainingDay, List<Restriction> restrictions) {
		Restriction restriction = findRestriction(restrictions, RestrictionType.WEEK);
		if (restriction != null) {
			LocalDate startDate = newTrainingDay
					.getScheduledDay()
					.toLocalDate()
					.with(ChronoField.DAY_OF_WEEK, 7)
					.minusWeeks(1);

			LocalDate endDate = newTrainingDay
					.getScheduledDay()
					.toLocalDate()
					.with(ChronoField.DAY_OF_WEEK, 1)
					.plusWeeks(1);

			if (findMatchingDays(existingTrainingDays, startDate, endDate).size() >= restriction.getDaysLimit()) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}
}
