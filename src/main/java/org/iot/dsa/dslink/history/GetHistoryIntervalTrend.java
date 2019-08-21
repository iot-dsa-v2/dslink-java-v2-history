package org.iot.dsa.dslink.history;

import java.util.Calendar;
import java.util.TimeZone;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.rollup.DSRollup;
import org.iot.dsa.rollup.RollupFunction;
import org.iot.dsa.table.DSITrend;
import org.iot.dsa.table.DSTrendWrapper;
import org.iot.dsa.time.Time;

/**
 * Rolls up values in a series to intervals.  Do not use if the interval
 * is INTERVAL_NONE.
 *
 * @author Aaron Hansen
 */
class GetHistoryIntervalTrend extends DSTrendWrapper {

    /////////////////////////////////////////////////////////////////
    // Instance Fields
    /////////////////////////////////////////////////////////////////

    private Calendar calendar;
    private boolean cov = false;
    private long curTs;
    private GetHistoryInterval interval;
    private long nextTs = -1;
    private RollupFunction rollup;
    private DSITrend trend;

    /////////////////////////////////////////////////////////////////
    // Constructors
    /////////////////////////////////////////////////////////////////

    /**
     * @param trend    Required, the trend to convert.
     * @param interval Required, duration representing the interval.
     * @param rollup   Required, how to aggregate multiple values in an interval.
     */
    public GetHistoryIntervalTrend(DSITrend trend,
                                   GetHistoryInterval interval,
                                   DSRollup rollup,
                                   boolean cov,
                                   TimeZone timeZone) {
        super(trend);
        calendar = Time.getCalendar();
        if (timeZone != null) {
            calendar.setTimeZone(timeZone);
        }
        this.trend = trend;
        this.interval = interval;
        this.rollup = rollup.getFunction();
        this.cov = cov;
        if (trend.next()) {
            nextTs = trend.getTimestamp();
            this.rollup.update(trend.getValue(), trend.getStatus());
        }
    }

    /////////////////////////////////////////////////////////////////
    // Public Methods
    /////////////////////////////////////////////////////////////////

    @Override
    public int getStatus() {
        return rollup.getStatus();
    }

    @Override
    public long getTimestamp() {
        return curTs;
    }

    @Override
    public DSElement getValue() {
        return rollup.getValue();
    }

    @Override
    public boolean next() {
        if (nextTs < 0) {
            return false;
        }
        long rowTs = trend.getTimestamp();
        curTs = nextTs;
        calendar.setTimeInMillis(nextTs);
        interval.next(calendar);
        nextTs = calendar.getTimeInMillis();
        if (nextTs <= rowTs) {
            if (cov) {
                return true;
            }
            //find the ivl for the next timestamp
            while (nextTs <= rowTs) {
                curTs = nextTs;
                calendar.setTimeInMillis(nextTs);
                interval.next(calendar);
                nextTs = calendar.getTimeInMillis();
            }
        }
        rollup.reset();
        while (trend.getTimestamp() < nextTs) {
            rollup.update(trend.getValue(), trend.getStatus());
            if (!trend.next()) {
                nextTs = -1;
                break;
            }
        }
        return true;
    }

}
