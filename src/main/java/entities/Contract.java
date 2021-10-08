package entities;

import lombok.Builder;
import lombok.Data;
import utils.DateUtils;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class Contract {
    private Long contractId;
    private BigDecimal premium;
    private Date startDate;
    private Date terminationDate;

    private List<Event> events;

    public void addEvent(Event e) {
        events.add(e);
    }

    public void removeEvent(Event e) {
        events.remove(e);
    }

    public boolean wasActiveMonth(Month month, Year year) {
        return startDate.compareTo(DateUtils.endOfMonth(month, year)) <= 0 &&
                terminationDate.compareTo(DateUtils.endOfMonthStartOfDay(month, year)) >= 0;
    }
}
