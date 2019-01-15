package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.table.DSITrend;
import org.iot.dsa.table.DSTrendWrapper;
import org.iot.dsa.time.DSDateTime;

/**
 * This trend uses records prior to the start time in an attempt to return a first row that
 * matches the starting timestamp.   This is for cov trends that collect at irregular intervals
 * and don't have nicely aligned timestamps.
 */
class CovStartTrend extends DSTrendWrapper {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    private static final int INIT = 0;
    private static final int PRE_REC = 1;
    private static final int POST_PRE = 2;
    private static final int NORMAL_TREND = 3;

    ////////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////
    private int preStatus;
    private long preTimestamp = -1;
    private DSElement preValue;
    private DSDateTime start;
    private int state = INIT;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public CovStartTrend(DSITrend trend, DSDateTime start) {
        super(trend);
        this.start = start;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getStatus() {
        if (state == PRE_REC) {
            return preStatus;
        }
        return super.getStatus();
    }

    @Override
    public long getTimestamp() {
        if (state == PRE_REC) {
            return preTimestamp;
        }
        return super.getTimestamp();
    }

    @Override
    public DSElement getValue() {
        if (state == PRE_REC) {
            return preValue;
        }
        return super.getValue();
    }

    @Override
    public DSIValue getValue(int index) {
        if (state == PRE_REC) {
            if (index == getTimestampColumn()) {
                return DSDateTime.valueOf(preTimestamp);
            }
            if (index == getValueColumn()) {
                return preValue;
            }
            if (index == getStatusColumn()) {
                return DSStatus.valueOf(preStatus);
            }
        }
        return super.getValue(index);
    }

    @Override
    public int getValueColumn() {
        return super.getValueColumn();
    }

    @Override
    public boolean next() {
        switch (state) {
            case INIT:
                return first();
            case PRE_REC:
                state = POST_PRE;
                return true;
            case POST_PRE:
                state = NORMAL_TREND;
        }
        return getInner().next();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Private Methods
    ///////////////////////////////////////////////////////////////////////////

    private boolean first() {
        long startTs = start.timeInMillis();
        long ts = -1;
        DSITrend trend = getInner();
        while (trend.next()) {
            ts = trend.getTimestamp();
            if (ts < startTs) {
                state = PRE_REC;
                preValue = trend.getValue();
                preTimestamp = startTs;
                preStatus = trend.getStatus();
            } else if (ts == startTs) {
                state = POST_PRE;
                break;
            } else { //ts > startTs
                if (preTimestamp > 0) {
                    state = PRE_REC;
                } else {
                    state = POST_PRE;
                }
                break;
            }
        }
        return state != INIT;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Inner Classes
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

}
