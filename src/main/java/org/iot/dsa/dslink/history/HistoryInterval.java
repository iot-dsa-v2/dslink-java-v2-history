package org.iot.dsa.dslink.history;

import java.util.Calendar;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSRegistry;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValue;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.time.DSTime;

/**
 * XML Schema compliant relative amount of time represented as a number of years, months, days,
 * hours, minutes, and seconds. The String format is -PnYnMnDTnHnMnS.
 *
 * @author Aaron Hansen
 */
public class HistoryInterval extends DSValue implements HistoryConstants {

    /////////////////////////////////////////////////////////////////
    // Class Fields
    /////////////////////////////////////////////////////////////////

    public static HistoryInterval NULL = new HistoryInterval(0, HistoryIntervalMode.OFF);
    private int count = 0;

    /////////////////////////////////////////////////////////////////
    // Instance Fields
    /////////////////////////////////////////////////////////////////
    public static DSAction editAction = new EditAction();
    private HistoryIntervalMode mode;
    private DSString string;

    /////////////////////////////////////////////////////////////////
    // Constructors
    /////////////////////////////////////////////////////////////////

    private HistoryInterval() {
    }

    private HistoryInterval(int count, HistoryIntervalMode mode) {
        this.count = count;
        this.mode = mode;
    }

    /////////////////////////////////////////////////////////////////
    // Public Methods
    /////////////////////////////////////////////////////////////////

    /**
     * Applies the duration to the given calendar and returns it.
     */
    public Calendar align(Calendar cal) {
        switch (mode) {
            case SECONDS:
                if (count < 60) {
                    DSTime.alignSeconds(count, cal);
                } else {
                    DSTime.alignSecond(cal);
                }
                break;
            case MINUTES:
                if (count < 60) {
                    DSTime.alignMinutes(count, cal);
                } else {
                    DSTime.alignMinute(cal);
                }
                break;
            case HOURS:
                if (count < 24) {
                    DSTime.alignHours(count, cal);
                } else {
                    DSTime.alignHour(cal);
                }
                break;
        }
        return cal;
    }

    /**
     * Applies the duration to the given calendar and returns it.
     */
    public Calendar apply(Calendar cal) {
        switch (mode) {
            case MILLIS:
                DSTime.addMillis(count, cal);
                break;
            case SECONDS:
                DSTime.addSeconds(count, cal);
                break;
            case MINUTES:
                DSTime.addMinutes(count, cal);
                break;
            case HOURS:
                DSTime.addHours(count, cal);
                break;
        }
        return cal;
    }

    @Override
    public HistoryInterval copy() {
        HistoryInterval ret = new HistoryInterval();
        ret.count = count;
        ret.mode = mode;
        ret.string = string;
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryInterval) {
            HistoryInterval d = (HistoryInterval) obj;
            return (d.count == count) && (d.mode == mode);
        }
        return false;
    }

    /**
     * String.
     */
    @Override
    public DSValueType getValueType() {
        return DSValueType.STRING;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Defaults to the equals method.
     */
    @Override
    public boolean isEqual(Object obj) {
        return equals(obj);
    }

    @Override
    public boolean isNull() {
        return this == NULL;
    }

    public boolean isOff() {
        return mode == HistoryIntervalMode.OFF;
    }

    @Override
    public DSElement toElement() {
        if (string == null) {
            toString();
        }
        return string;
    }

    /**
     * The number of millis in the interval.
     */
    public long toMillis() {
        int ms = 1;
        switch (mode) {
            case SECONDS:
                ms = 1000;
                break;
            case MINUTES:
                ms = DSTime.MILLIS_MINUTE;
                break;
            case HOURS:
                ms = DSTime.MILLIS_HOUR;
                break;
        }
        return count * ms;
    }

    /**
     * String representation of this duration.
     */
    @Override
    public String toString() {
        if (string != null) {
            return string.toString();
        }
        if (this == NULL) {
            return "null";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(count);
        buf.append(' ');
        buf.append(mode.toString());
        return buf.toString();
    }

    @Override
    public HistoryInterval valueOf(DSElement element) {
        if ((element == null) || element.isNull()) {
            return NULL;
        }
        return valueOf(element.toString());
    }

    /**
     * Parses a duration using the format: &lt;n&gt; &lt;s | m | h&gt;
     */
    public static HistoryInterval valueOf(String s) {
        if ((s == null) || s.isEmpty() || s.equals("null")) {
            return NULL;
        }
        if (s.equals(OFF)) {
            return NULL;
        }
        String[] ary = s.trim().split(" ");
        if (ary.length != 2) {
            throw new IllegalArgumentException("Illegal interval: " + s);
        }
        HistoryInterval ret = new HistoryInterval();
        ret.count = Integer.parseInt(ary[0]);
        if (ret.count < 0) {
            throw new IllegalArgumentException("Illegal interval: " + s);
        }
        ret.mode = HistoryIntervalMode.valueFor(ary[1]);
        ret.string = DSString.valueOf(s);
        if ((ret.count == 0) && (ret.mode == HistoryIntervalMode.OFF)) {
            return NULL;
        }
        return ret;
    }

    /////////////////////////////////////////////////////////////////
    // Private Methods
    /////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////
    // Inner Classes
    /////////////////////////////////////////////////////////////////

    private static class EditAction extends DSAction implements HistoryConstants {

        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            int val = invocation.getParameters().getInt(VALUE);
            HistoryIntervalMode mode = HistoryIntervalMode
                    .valueFor(invocation.getParameters().getString(MODE));
            target.getParent().put(target, new HistoryInterval(val, mode));
            return null;
        }

        @Override
        public void prepareParameter(DSInfo target, DSMap parameter) {
        }

        {
            addParameter(VALUE, DSInt.NULL, "Interval count");
            addParameter(MODE, HistoryIntervalMode.OFF, "Interval units");
        }
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryInterval.class, NULL);
    }

}
