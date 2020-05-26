package distributed.monolith.learninghive.service.util;

import java.time.LocalDate;

public final class DateUtil {

	private DateUtil() {
	}

	public static boolean isPastDate(java.sql.Date date) {
		return LocalDate.now().isAfter(date.toLocalDate());
	}

	public static boolean areEqual(java.sql.Date date1, java.sql.Date date2) {
		return date1.toLocalDate().equals(date2.toLocalDate());
	}
}
