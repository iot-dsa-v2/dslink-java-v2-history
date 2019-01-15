package org.iot.dsa.dslink.history;

import java.util.HashMap;
import java.util.Map;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIEnum;
import org.iot.dsa.node.DSIObject;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSRegistry;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.DSValueType;

/**
 * Enum that describes a history group interval.
 *
 * @author Aaron Hansen
 */
public enum HistoryIntervalMode implements DSIEnum, DSIValue {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    MILLIS(HistoryConstants.MILLIS),
    SECONDS(HistoryConstants.SECONDS),
    MINUTES(HistoryConstants.MINUTES),
    HOURS(HistoryConstants.HOURS),
    OFF(HistoryConstants.OFF);

    private static final Map<String, HistoryIntervalMode> enums = new HashMap<String, HistoryIntervalMode>();

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSString element;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    private HistoryIntervalMode(String display) {
        this.element = DSString.valueOf(display);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DSIObject copy() {
        return this;
    }

    @Override
    public DSList getEnums(DSList bucket) {
        if (bucket == null) {
            bucket = new DSList();
        }
        for (HistoryIntervalMode e : values()) {
            bucket.add(e.toElement());
        }
        return bucket;
    }

    @Override
    public DSValueType getValueType() {
        return DSValueType.ENUM;
    }

    @Override
    public boolean isEqual(Object obj) {
        if (obj == this) {
            return true;
        }
        return element.equals(obj);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public DSElement toElement() {
        return element;
    }

    @Override
    public String toString() {
        return element.toString();
    }

    /**
     * Get an instance from a string.
     */
    public static HistoryIntervalMode valueFor(String display) {
        HistoryIntervalMode ret = enums.get(display);
        if (ret == null) {
            ret = enums.get(display.toLowerCase());
        }
        return ret;
    }

    @Override
    public DSIValue valueOf(DSElement element) {
        return valueFor(element.toString());
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryIntervalMode.class, SECONDS);
        for (HistoryIntervalMode e : SECONDS.values()) {
            enums.put(e.name(), e);
            enums.put(e.toString(), e);
            enums.put(e.toString().toLowerCase(), e);
        }
    }

}//HistoryIntervalMode
