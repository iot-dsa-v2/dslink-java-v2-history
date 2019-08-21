package org.iot.dsa.dslink.history;

import java.util.Calendar;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSMetadata;
import org.iot.dsa.node.DSRegistry;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValue;
import org.iot.dsa.node.DSValueType;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSISetAction;
import org.iot.dsa.time.Time;

/**
 * XML Schema compliant relative amount of time represented as a number of years, months, days,
 * hours, minutes, and seconds. The String format is -PnYnMnDTnHnMnS.
 *
 * @author Aaron Hansen
 */
public class HistoryInterval extends DSValue implements DSISetAction, HistoryConstants {

    /////////////////////////////////////////////////////////////////
    // Class Fields
    /////////////////////////////////////////////////////////////////

    public static HistoryInterval NULL = new HistoryInterval(0, HistoryIntervalMode.OFF);
    private int count = 0;

    /////////////////////////////////////////////////////////////////
    // Instance Fields
    /////////////////////////////////////////////////////////////////

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
                    Time.alignSeconds(count, cal);
                } else {
                    Time.alignSecond(cal);
                }
                break;
            case MINUTES:
                if (count < 60) {
                    Time.alignMinutes(count, cal);
                } else {
                    Time.alignMinute(cal);
                }
                break;
            case HOURS:
                if (count < 24) {
                    Time.alignHours(count, cal);
                } else {
                    Time.alignHour(cal);
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
                Time.addMillis(count, cal);
                break;
            case SECONDS:
                Time.addSeconds(count, cal);
                break;
            case MINUTES:
                Time.addMinutes(count, cal);
                break;
            case HOURS:
                Time.addHours(count, cal);
                break;
        }
        return cal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryInterval) {
            HistoryInterval d = (HistoryInterval) obj;
            return (d.count == count) && (d.mode == mode);
        }
        return false;
    }

    @Override
    public DSAction getSetAction() {
        return SetAction.INSTANCE;
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
                ms = Time.MILLIS_MINUTE;
                break;
            case HOURS:
                ms = Time.MILLIS_HOUR;
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
            string = DSString.NULL;
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append(count);
            buf.append(' ');
            buf.append(mode.toString());
            string = DSString.valueOf(buf.toString());
        }
        return string.toString();
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

    public static class SetAction extends DSAction {

        public static final SetAction INSTANCE = new SetAction();

        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            int val = invocation.getParameters().getInt(COUNT);
            HistoryIntervalMode mode = HistoryIntervalMode
                    .valueFor(invocation.getParameters().getString(MODE));
            target.getParent().put(target, new HistoryInterval(val, mode));
            return null;
        }

        @Override
        public void prepareParameter(DSInfo target, DSMap parameter) {
            HistoryInterval node = (HistoryInterval) target.get();
            if (parameter.get(DSMetadata.NAME).equals(COUNT)) {
                parameter.put(DSMetadata.DEFAULT, node.count);
            } else {
                parameter.put(DSMetadata.DEFAULT, node.mode.toElement());
            }
        }

        {
            addParameter(COUNT, DSInt.NULL, "Interval count");
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
