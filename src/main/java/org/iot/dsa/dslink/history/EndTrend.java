package org.iot.dsa.dslink.history;

import org.iot.dsa.table.DSITrend;
import org.iot.dsa.table.DSTrendWrapper;
import org.iot.dsa.time.DSDateTime;

/**
 * Enforces an ending timestamp of a time range.
 *
 * @author Aaron Hansen
 */
class EndTrend extends DSTrendWrapper {

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
            if (inner.getTimestamp() < end) {
                return true;
            }
        }
        return false;
    }

}
