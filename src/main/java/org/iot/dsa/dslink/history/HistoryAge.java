package org.iot.dsa.dslink.history;

import java.util.Calendar;
import java.util.TimeZone;
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
import org.iot.dsa.time.DSDateTime;
import org.iot.dsa.time.Time;

/**
 * XML Schema compliant relative amount of time represented as a number of years, months, days,
 * hours, minutes, and seconds. The String format is -PnYnMnDTnHnMnS.
 *
 * @author Aaron Hansen
 */
public class HistoryAge extends DSValue implements DSISetAction, HistoryConstants {

    /////////////////////////////////////////////////////////////////
    // Class Fields
    /////////////////////////////////////////////////////////////////

    public static HistoryAge NULL = new HistoryAge(0, HistoryAgeMode.OFF);
    private int count = 0;

    /////////////////////////////////////////////////////////////////
    // Instance Fields
    /////////////////////////////////////////////////////////////////

    private HistoryAgeMode mode;
    private DSString string;

    /////////////////////////////////////////////////////////////////
    // Constructors
    /////////////////////////////////////////////////////////////////

    private HistoryAge() {
    }

    private HistoryAge(int count, HistoryAgeMode mode) {
        this.count = count;
        this.mode = mode;
    }

    /////////////////////////////////////////////////////////////////
    // Public Methods
    /////////////////////////////////////////////////////////////////

    /**
     * Applies the duration to the given calendar and returns it.
     */
    public Calendar apply(Calendar cal) {
        int count = this.count;
        if (count == 0) {
            return cal;
        } else if (count > 0) {
            count = -count;
        }
        switch (mode) {
            case MINUTES:
                Time.addMinutes(count, cal);
                break;
            case HOURS:
                Time.addHours(count, cal);
                break;
            case DAYS:
                Time.addDays(count, cal);
                break;
            case WEEKS:
                Time.addWeeks(count, cal);
                break;
            case MONTHS:
                Time.addMonths(count, cal);
                break;
        }
        return cal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HistoryAge) {
            HistoryAge d = (HistoryAge) obj;
            return (d.count == count) && (d.mode == mode);
        }
        return false;
    }

    /**
     * Applies the age to the current time.
     */
    public DSDateTime fromNow(TimeZone timeZone) {
        Calendar cal = Time.getCalendar(System.currentTimeMillis(), timeZone);
        apply(cal);
        DSDateTime ret = DSDateTime.valueOf(cal);
        Time.recycle(cal);
        return ret;
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
        return mode == HistoryAgeMode.OFF;
    }

    @Override
    public DSElement toElement() {
        if (string == null) {
            toString();
        }
        return string;
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
    public HistoryAge valueOf(DSElement element) {
        if ((element == null) || element.isNull()) {
            return NULL;
        }
        return valueOf(element.toString());
    }

    /**
     * Parses a duration using the format: &lt;n&gt; &lt;s | m | h&gt;
     */
    public static HistoryAge valueOf(String s) {
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
        HistoryAge ret = new HistoryAge();
        ret.count = Integer.parseInt(ary[0]);
        if (ret.count < 0) {
            throw new IllegalArgumentException("Illegal interval: " + s);
        }
        ret.mode = HistoryAgeMode.valueFor(ary[1]);
        ret.string = DSString.valueOf(s);
        if ((ret.count == 0) && (ret.mode == HistoryAgeMode.OFF)) {
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
            HistoryAgeMode mode = HistoryAgeMode
                    .valueFor(invocation.getParameters().getString(MODE));
            target.getParent().put(target, new HistoryAge(val, mode));
            return null;
        }

        @Override
        public void prepareParameter(DSInfo target, DSMap parameter) {
            HistoryAge node = (HistoryAge) target.get();
            if (parameter.get(DSMetadata.NAME).equals(COUNT)) {
                parameter.put(DSMetadata.DEFAULT, node.count);
            } else {
                parameter.put(DSMetadata.DEFAULT, node.mode.toElement());
            }
        }

        {
            addParameter(COUNT, DSInt.NULL, null);
            addParameter(MODE, HistoryAgeMode.OFF, "Units");
        }
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryAge.class, NULL);
    }

}
