package distributed.monolith.learninghive.restrictions;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
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
			int startMonth;
			int endMonth;
			if (scheduledDate.getMonthValue() <= 3) {
				startMonth = 1;
				endMonth = 3;
			} else if (scheduledDate.getMonthValue() <= 6) {
				startMonth = 4;
				endMonth = 6;
			} else if (scheduledDate.getMonthValue() <= 9) {
				startMonth = 7;
				endMonth = 9;
			} else {
				startMonth = 10;
				endMonth = 12;
			}

			LocalDate startDate = LocalDate.of(scheduledDate.getYear(), startMonth, 1).minusDays(1);

			LocalDate endDate = LocalDate.of(scheduledDate.getYear(), endMonth, 1);
			endDate = endDate.withDayOfMonth(endDate.lengthOfMonth()).plusDays(1);

			if (findMatchingDays(existingTrainingDays, startDate, endDate).size() > restriction
					.getDaysLimit() - 1) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}
}
