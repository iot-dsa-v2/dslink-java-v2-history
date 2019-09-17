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
 * Enum that describes the history type.
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

    private static final Map<String, HistoryType> enums = new HashMap<String, HistoryType>();

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSString element;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    private HistoryType(String display) {
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

    public boolean isUnknown() {
        return this == UNKNOWN;
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
    public DSIValue valueOf(DSElement element) {
        return valueFor(element.toString());
    }

    /////////////////////////////////////////////////////////////////
    // Initialization
    /////////////////////////////////////////////////////////////////

    static {
        DSRegistry.registerDecoder(HistoryType.class, BOOLEAN);
        for (HistoryType e : BOOLEAN.values()) {
            enums.put(e.name(), e);
            enums.put(e.toString(), e);
            enums.put(e.toString().toLowerCase(), e);
        }
    }

}
