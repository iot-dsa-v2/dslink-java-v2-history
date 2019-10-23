package org.iot.dsa.dslink.history.table;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSMap;

/**
 * A convenience for a trend that wraps another trend.
 *
 * @author Aaron Hansen
 */
public class DSTrendWrapper implements DSITrend {

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSITrend inner;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public DSTrendWrapper(DSITrend inner) {
        this.inner = inner;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getColumnCount() {
        return inner.getColumnCount();
    }

    @Override
    public void getColumnMetadata(int index, DSMap bucket) {
        inner.getColumnMetadata(index, bucket);
    }

    @Override
    public DSIValue getValue(int index) {
        return inner.getValue(index);
    }

    public DSITrend getInner() {
        return inner;
    }

    @Override
    public int getStatus() {
        return inner.getStatus();
    }

    @Override
    public int getStatusColumn() {
        return inner.getStatusColumn();
    }

    @Override
    public long getTimestamp() {
        return inner.getTimestamp();
    }

    @Override
    public int getTimestampColumn() {
        return inner.getTimestampColumn();
    }

    @Override
    public DSElement getValue() {
        return inner.getValue();
    }

    @Override
    public int getValueColumn() {
        return inner.getValueColumn();
    }

    @Override
    public boolean next() {
        return false;
    }

}
