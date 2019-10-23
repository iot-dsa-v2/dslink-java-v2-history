package org.iot.dsa.dslink.history.value;

import org.iot.dsa.dslink.history.HistoryConstants;
import org.iot.dsa.node.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that describes a HistoryAge.
 *
 * @author Aaron Hansen
 */
public enum HistoryAgeMode implements DSIEnum, DSIValue {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    MINUTES(HistoryConstants.MINUTES),
    HOURS(HistoryConstants.HOURS),
    DAYS(HistoryConstants.DAYS),
    WEEKS(HistoryConstants.WEEKS),
    MONTHS(HistoryConstants.MONTHS),
    OFF(HistoryConstants.OFF);

    private static final Map<String, HistoryAgeMode> enums = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryAgeMode.class, OFF);
        for (HistoryAgeMode e : HistoryAgeMode.values()) {
            enums.put(e.name(), e);
            enums.put(e.toString(), e);
            enums.put(e.toString().toLowerCase(), e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    private DSString element;

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    HistoryAgeMode(String display) {
        this.element = DSString.valueOf(display);
    }

    /**
     * Get an instance from a string.
     */
    public static HistoryAgeMode valueFor(String display) {
        HistoryAgeMode ret = enums.get(display);
        if (ret == null) {
            ret = enums.get(display.toLowerCase());
        }
        return ret;
    }

    @Override
    public DSIObject copy() {
        return this;
    }

    @Override
    public DSList getEnums(DSList bucket) {
        if (bucket == null) {
            bucket = new DSList();
        }
        for (HistoryAgeMode e : values()) {
            bucket.add(e.toElement());
        }
        return bucket;
    }

    @Override
    public DSValueType getValueType() {
        return DSValueType.ENUM;
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
    public DSIValue valueOf(DSElement element) {
        return valueFor(element.toString());
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return element.toString();
    }

}
