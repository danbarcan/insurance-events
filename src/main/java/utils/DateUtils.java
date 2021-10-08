package utils;

import java.time.Month;
import java.time.Year;
import java.util.Date;

public class DateUtils {
    public static final Year YEAR_2020 = Year.of(2020);

    public static Date endOfMonth(Month month, Year year) {
        return new Date(year.getValue() - 1900, month.getValue() - 1, year.isLeap() ? month.maxLength() : month.minLength(), 23, 59, 59);
    }

    public static Date endOfMonthStartOfDay(Month month, Year year) {
        return new Date(year.getValue() - 1900, month.getValue() - 1, year.isLeap() ? month.maxLength() : month.minLength());
    }

    public static boolean dateIsInMonth(Date date, Month month, Year year) {
        return date.getMonth() == month.getValue() - 1 && date.getYear() == year.getValue() - 1900;
    }
}
