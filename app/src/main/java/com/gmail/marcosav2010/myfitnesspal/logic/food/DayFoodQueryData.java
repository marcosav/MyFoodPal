package com.gmail.marcosav2010.myfitnesspal.logic.food;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class DayFoodQueryData {

    private String meals;
    private boolean buy;
    private Date date;
    private Date toDate;

    public Set<Date> getDates() {
        Set<Date> datesInRange = new HashSet<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(buy ? toDate : date);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return datesInRange;
    }
}
