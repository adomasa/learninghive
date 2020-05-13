package distributed.monolith.learninghive.restrictions;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
import java.util.List;

public class YearRestrictionValidator extends RestrictionValidatorDecorator {
	public YearRestrictionValidator(RestrictionValidator decorator) {
		super(decorator);
	}

	@Override
	public Restriction findViolatedRestriction(List<TrainingDay> existingTrainingDays,
	                                           TrainingDay newTrainingDay, List<Restriction> restrictions) {
		Restriction restriction = findRestriction(restrictions, RestrictionType.YEAR);
		if (restriction != null) {
			LocalDate startDate = newTrainingDay
					.getScheduledDay()
					.toLocalDate()
					.withDayOfYear(1)
					.minusDays(1);

			LocalDate endDate = newTrainingDay
					.getScheduledDay()
					.toLocalDate()
					.withDayOfYear(1)
					.plusYears(1);

			if (findMatchingDays(existingTrainingDays, startDate, endDate).size() > restriction
					.getDaysLimit() - 1) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}
}
