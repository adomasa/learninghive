package distributed.monolith.learninghive.restrictions;


import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.domain.TrainingDay;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DayRestrictionValidator extends RestrictionValidatorDecorator {
	public DayRestrictionValidator(RestrictionValidator decorator) {
		super(decorator);
	}

	@Override
	public Restriction findViolatedRestriction(List<TrainingDay> existingTrainingDays,
	                                           TrainingDay newTrainingDay, List<Restriction> restrictions) {
		Restriction restriction = findRestriction(restrictions, RestrictionType.DAYS_IN_A_ROW);
		if (restriction != null) {
			LocalDate date = newTrainingDay.getScheduledDay().toLocalDate();
			List<LocalDate> existingsDates = existingTrainingDays
					.stream()
					.map(t -> t.getScheduledDay().toLocalDate())
					.collect(Collectors.toList());

			long daysInRow = findDaysInRowCount(existingsDates, date, restriction.getDaysLimit(), true) +
					findDaysInRowCount(existingsDates, date, restriction.getDaysLimit(), false);
			if (daysInRow > restriction.getDaysLimit() - 1) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}

	@SuppressWarnings("PMD.AvoidReassigningParameters")
	private long findDaysInRowCount(List<LocalDate> days, LocalDate targetDate, long limit, boolean forward) {
		long count = 0;
		long daysToAdd = forward ? 1 : -1;

		while (count < limit) {
			targetDate = targetDate.plusDays(daysToAdd);
			LocalDate finalTargetDate = targetDate;

			if (days.stream().filter(d -> d.equals(finalTargetDate)).findFirst().isEmpty()) {
				break;
			}

			count++;
		}

		return count;
	}
}
