package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSElement;
import org.iot.dsa.table.DSIResultsCursor;

/**
 * A time series row cursor, but provides methods for primitive access.  For performance reasons,
 * implementations should not convert the status bits and timestamp into objects unless the column
 * is accessed via the DSIResultsCursor methods.
 *
 * @author Aaron Hansen
 */
public interface DSITrend extends DSIResultsCursor {

    public int getStatus();

    public int getStatusColumn();

    public long getTimestamp();

    public int getTimestampColumn();

    public DSElement getValue();

    public int getValueColumn();

}
