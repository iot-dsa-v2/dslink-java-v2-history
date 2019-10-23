package org.iot.dsa.dslink.history.value;

import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.dslink.history.HistoryConstants;
import org.iot.dsa.node.*;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIActionRequest;
import org.iot.dsa.node.action.DSISetAction;
import org.iot.dsa.time.Time;

import java.util.Calendar;

/**
 * Used to determine the collection interval of a history group.
 *
 * @author Aaron Hansen
 */
public class HistoryInterval extends DSValue implements DSISetAction, HistoryConstants {

    /////////////////////////////////////////////////////////////////
    // Class Fields
    /////////////////////////////////////////////////////////////////

    public static HistoryInterval NULL = new HistoryInterval(0, HistoryIntervalMode.OFF);

    static {
        DSRegistry.registerDecoder(HistoryInterval.class, NULL);
    }

    /////////////////////////////////////////////////////////////////
    // Instance Fields
    /////////////////////////////////////////////////////////////////

    private int count = 0;
    private HistoryIntervalMode mode;

    /////////////////////////////////////////////////////////////////
    // Constructors
    /////////////////////////////////////////////////////////////////
    private DSString string;

    private HistoryInterval() {
    }

    /////////////////////////////////////////////////////////////////
    // Public Methods
    /////////////////////////////////////////////////////////////////

    private HistoryInterval(int count, HistoryIntervalMode mode) {
        this.count = count;
        this.mode = mode;
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
    public boolean isNull() {
        return this == NULL;
    }

    @Override
    public DSElement toElement() {
        if (string == null) {
            toString();
        }
        return string;
    }

    @Override
    public HistoryInterval valueOf(DSElement element) {
        if ((element == null) || element.isNull()) {
            return NULL;
        }
        return valueOf(element.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryInterval) {
            HistoryInterval d = (HistoryInterval) obj;
            return (d.count == count) && (d.mode == mode);
        }
        return false;
    }

    public boolean isOff() {
        return mode == HistoryIntervalMode.OFF;
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

    /////////////////////////////////////////////////////////////////
    // Private Methods
    /////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////
    // Inner Classes
    /////////////////////////////////////////////////////////////////

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

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    public static class SetAction extends DSAction {

        public static final SetAction INSTANCE = new SetAction();

        {
            addParameter(COUNT, DSInt.NULL, "Interval count");
            addParameter(MODE, HistoryIntervalMode.OFF, "Interval units");
        }

        @Override
        public ActionResults invoke(DSIActionRequest req) {
            int val = req.getParameters().getInt(COUNT);
            HistoryIntervalMode mode = HistoryIntervalMode
                    .valueFor(req.getParameters().getString(MODE));
            DSInfo<?> target = req.getTargetInfo();
            target.getParent().put(target, new HistoryInterval(val, mode));
            return null;
        }

        @Override
        public void prepareParameter(DSInfo<?> target, DSMap parameter) {
            HistoryInterval node = (HistoryInterval) target.get();
            if (parameter.get(DSMetadata.NAME).equals(COUNT)) {
                parameter.put(DSMetadata.DEFAULT, node.count);
            } else {
                parameter.put(DSMetadata.DEFAULT, node.mode.toElement());
            }
        }
    }

}
