package org.iot.dsa.dslink.history.value;

import org.iot.dsa.time.Time;

import java.util.Calendar;

/**
 * Interval used by the getHistory action.
 *
 * @author Aaron Hansen
 */
public class GetHistoryInterval {

    private boolean alignDays;
    private boolean alignHours;
    private boolean alignMinutes;
    private boolean alignMonths;
    private boolean alignSeconds;
    private boolean alignWeeks;
    private boolean alignYears;
    private int days = -1;
    private int hours = -1;
    private int minutes = -1;
    private int months = -1;
    private int seconds = -1;
    private int weeks = -1;
    private int years = -1;

    public static GetHistoryInterval valueOf(String interval) {
        if (interval == null
                || interval.isEmpty()
                || "null".equals(interval)
                || "none".equals(interval)
                || "default".equals(interval)) {
            return null;
        }
        final GetHistoryInterval parser = new GetHistoryInterval();
        char[] chars = interval.toCharArray();
        StringBuilder number = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                parser.update(c, number.toString());
                number.delete(0, number.length());
            }
        }
        if (number.length() > 0) {
            throw new RuntimeException("Invalid expression");
        }
        return parser;
    }

    /**
     * If configured to align to an interval, this will align the given calendar.
     *
     * @return True if the calendar was modified.
     */
    public boolean align(Calendar calendar) {
        boolean modified = false;
        if (alignSeconds) {
            Time.alignSeconds(seconds, calendar);
            modified = true;
        }
        if (alignMinutes) {
            Time.alignMinutes(minutes, calendar);
            modified = true;
        }
        if (alignHours) {
            Time.alignHour(calendar);
            modified = true;
        }
        if (alignDays) {
            Time.alignDay(calendar);
            modified = true;
        }
        if (alignWeeks) {
            Time.alignWeek(calendar);
            modified = true;
        }
        if (alignMonths) {
            Time.alignMonth(calendar);
            modified = true;
        }
        if (alignYears) {
            Time.alignYear(calendar);
            modified = true;
        }
        return modified;
    }

    /**
     * Advances the calendar, without performing any alignment.
     *
     * @return True if the calendar was modified.
     */
    public boolean next(Calendar calendar) {
        boolean modified = false;
        if (seconds > 0) {
            Time.addSeconds(seconds, calendar);
            modified = true;
        }
        if (minutes > 0) {
            Time.addMinutes(minutes, calendar);
        }
        if (hours > 0) {
            Time.addHours(hours, calendar);
            modified = true;
        }
        if (days > 0) {
            Time.addDays(days, calendar);
            modified = true;
        }
        if (weeks > 0) {
            Time.addWeeks(weeks, calendar);
            modified = true;
        }
        if (months > 0) {
            Time.addMonths(months, calendar);
            modified = true;
        }
        if (years > 0) {
            Time.addYears(years, calendar);
            modified = true;
        }
        return modified;
    }

    private void check(String type, int num) {
        if (num != -1) {
            throw new RuntimeException(type + " is already set");
        }
    }

    private void update(char interval, String number) {
        int num = Integer.parseInt(number);
        switch (interval) {
            case 'S':
                alignSeconds = true;
            case 's':
                check("seconds", seconds);
                seconds = num;
                break;
            case 'M':
                alignMinutes = true;
            case 'm':
                check("minutes", minutes);
                minutes = num;
                break;
            case 'H':
                alignHours = true;
            case 'h':
                check("hours", hours);
                hours = num;
                break;
            case 'D':
                alignDays = true;
            case 'd':
                check("days", days);
                days = num;
                break;
            case 'W':
                alignWeeks = true;
            case 'w':
                check("weeks", weeks);
                weeks = num;
                break;
            case 'N':
                alignMonths = true;
            case 'n':
                check("months", months);
                months = num;
                break;
            case 'Y':
                alignYears = true;
            case 'y':
                check("years", years);
                years = num;
                break;
            default:
                throw new RuntimeException("Unknown char: " + interval);
        }
    }

}
