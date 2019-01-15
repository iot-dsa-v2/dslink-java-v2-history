package org.iot.dsa.dslink.history;

import java.util.Calendar;
import org.iot.dsa.time.DSTime;

/**
 * Interval used by the GetHistory action.
 *
 * @author Aaron Hansen
 */
class GetHistoryInterval {

    private boolean alignDays;
    private boolean alignHours;
    private boolean alignMinutes;
    private boolean alignMonths;
    private boolean alignSeconds;
    private boolean alignWeeks;
    private boolean alignYears;
    private Calendar cache;
    private int days = -1;
    private int hours = -1;
    private int minutes = -1;
    private int months = -1;
    private int seconds = -1;
    private int weeks = -1;
    private int years = -1;

    /**
     * If configured to align to an interval, this will align the given calendar.
     *
     * @return True if the calendar was modified.
     */
    public boolean align(Calendar calendar) {
        boolean modified = false;
        if (alignSeconds) {
            DSTime.alignSeconds(seconds, calendar);
            modified = true;
        }
        if (alignMinutes) {
            DSTime.alignMinutes(minutes, calendar);
            modified = true;
        }
        if (alignHours) {
            DSTime.alignHour(calendar);
            modified = true;
        }
        if (alignDays) {
            DSTime.alignDay(calendar);
            modified = true;
        }
        if (alignWeeks) {
            DSTime.alignWeek(calendar);
            modified = true;
        }
        if (alignMonths) {
            DSTime.alignMonth(calendar);
            modified = true;
        }
        if (alignYears) {
            DSTime.alignYear(calendar);
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
            DSTime.addSeconds(seconds, calendar);
            modified = true;
        }
        if (minutes > 0) {
            DSTime.addMinutes(minutes, calendar);
        }
        if (hours > 0) {
            DSTime.addHours(hours, calendar);
            modified = true;
        }
        if (days > 0) {
            DSTime.addDays(days, calendar);
            modified = true;
        }
        if (weeks > 0) {
            DSTime.addWeeks(weeks, calendar);
            modified = true;
        }
        if (months > 0) {
            DSTime.addMonths(months, calendar);
            modified = true;
        }
        if (years > 0) {
            DSTime.addYears(years, calendar);
            modified = true;
        }
        return modified;
    }

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
