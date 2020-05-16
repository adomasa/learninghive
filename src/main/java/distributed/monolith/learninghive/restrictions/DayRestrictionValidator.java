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
			List<LocalDate> existingDates = existingTrainingDays
					.stream()
					.map(t -> t.getScheduledDay().toLocalDate())
					.collect(Collectors.toList());

			long daysInRow = findDaysInRowCount(existingDates, date, restriction.getDaysLimit(), true) +
					findDaysInRowCount(existingDates, date, restriction.getDaysLimit(), false);
			if (daysInRow >= restriction.getDaysLimit()) {
				return restriction;
			}
		}
		return decorator.findViolatedRestriction(existingTrainingDays, newTrainingDay, restrictions);
	}

	// forward parameters specifies whether to look for days ahead of target date or before
	private long findDaysInRowCount(List<LocalDate> days, LocalDate targetDate, long dayLimit, boolean forward) {
		long count = 0;
		long daysToAdd = forward ? 1 : -1;
		LocalDate targetDateCopy = targetDate;
		while (count < dayLimit) {
			targetDateCopy = targetDateCopy.plusDays(daysToAdd);
			LocalDate finalTargetDate = targetDateCopy;

			if (!days.stream().anyMatch(d -> d.equals(finalTargetDate))) {
				break;
			}

			count++;
		}

		return count;
	}
}
