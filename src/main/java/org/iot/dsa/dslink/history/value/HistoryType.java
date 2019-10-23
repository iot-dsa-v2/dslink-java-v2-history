package org.iot.dsa.dslink.history.value;

import org.iot.dsa.node.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that describes the history data type.
 *
 * @author Aaron Hansen
 */
public enum HistoryType implements DSIEnum, DSIValue {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    UNKNOWN("Unknown"),
    BOOLEAN("Boolean"),
    BYTES("Bytes"),
    DOUBLE("Double"),
    FLOAT("Float"),
    INT("Integer"),
    LIST("List"),
    LONG("Long"),
    MAP("Map"),
    STRING("String");

    private static final Map<String, HistoryType> enums = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryType.class, BOOLEAN);
        for (HistoryType e : HistoryType.values()) {
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

    HistoryType(String display) {
        this.element = DSString.valueOf(display);
    }

    /**
     * Get an instance from a string.
     */
    public static HistoryType valueFor(String display) {
        HistoryType ret = enums.get(display);
        if (ret == null) {
            ret = enums.get(display.toLowerCase());
        }
        return ret;
    }

    /**
     * Choose the best enum for the given element.
     */
    public static HistoryType valueFor(DSElement element) {
        if (element == null) {
            return UNKNOWN;
        }
        switch (element.getElementType()) {
            case BOOLEAN:
                return BOOLEAN;
            case BYTES:
                return BYTES;
            case DOUBLE:
                return DOUBLE;
            case LIST:
                return LIST;
            case LONG:
                return LONG;
            case MAP:
                return MAP;
            case STRING:
                return STRING;
        }
        return UNKNOWN;
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
        for (HistoryType e : values()) {
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

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return element.toString();
    }

}
