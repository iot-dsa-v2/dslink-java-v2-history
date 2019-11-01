package org.iot.dsa.dslink.history.table;

import org.iot.dsa.time.DSDateTime;

/**
 * Enforces an ending timestamp of a time range.
 *
 * @author Aaron Hansen
 */
public class EndTrend extends DSTrendWrapper {

    ////////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private long end;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public EndTrend(DSITrend trend, DSDateTime end) {
        super(trend);
        this.end = end.timeInMillis();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean next() {
        DSITrend inner = getInner();
        if (inner.next()) {
            return inner.getTimestamp() < end;
        }
        return false;
    }

}