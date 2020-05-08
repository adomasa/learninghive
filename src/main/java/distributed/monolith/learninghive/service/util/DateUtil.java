package distributed.monolith.learninghive.service.util;

import distributed.monolith.learninghive.model.exception.ChangingPastTrainingDayException;

import java.time.LocalDate;

public final class DateUtil {

	private DateUtil() {
	}

	public static void throwIfPastDate(java.sql.Date date) throws ChangingPastTrainingDayException {
		if (LocalDate.now().isAfter((date.toLocalDate()))) {
			throw new ChangingPastTrainingDayException();
		}
	}
}
