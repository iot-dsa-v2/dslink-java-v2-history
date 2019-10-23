package org.iot.dsa.dslink.history;

import org.iot.dsa.dslink.history.table.CovStartTrend;
import org.iot.dsa.dslink.history.table.DSITrend;
import org.iot.dsa.node.*;
import org.iot.dsa.time.DSDateTime;
import org.iot.dsa.time.DSTimeRange;

/**
 * Base class for binding an implementation to a specific type of database.
 *
 * @author Aaron Hansen
 */
public abstract class HistoryProvider implements HistoryConstants {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The earliest record for the series.
     */
    public abstract DSDateTime getFirstTimestamp(History history);

    /**
     * The total number of records in the series.
     */
    public abstract int getRecordCount(History history);

    /**
     * Return a cursor for the given history and start time.  The cursor should walk until
     * there are no more records in the underlying database.
     */
    public abstract DSITrend getTrend(History history, DSDateTime start);

    /**
     * This is called when the backing history is change of value and a record for the start time
     * is desired.  The default implementation subtracts 24 hours from the start in an attempt to
     * find the appropriate start time.  If the underlying implementation can do it without
     * guessing and searching, it should override this method to provide a better
     * implementation.
     *
     * @param history The history for which a trend is desired.
     * @param start   Used to determine the earliest record of the trend.
     */
    public DSITrend getTrendCov(History history, DSDateTime start) {
        return new CovStartTrend(getTrend(history, start.prevDay()), start);
    }

    /**
     * Called when the history is created and at startup.  Does nothing by default.
     */
    public void init(History history) {
    }

    /**
     * Create a new node representing a database.
     *
     * @param parameters Parameters supplied to the action for creating a new database.
     * @return New node representing a database.
     */
    public abstract HistoryDatabase makeDatabaseNode(DSMap parameters);

    /**
     * Create a new folder for history groups. Returns a new HistoryGroupFolder by default.
     *
     * @param parameters Parameters supplied to the action for creating a new folder.
     * @return New node representing a history group folder.
     */
    public HistoryGroupFolder makeGroupFolder(DSMap parameters) {
        return new HistoryGroupFolder();
    }

    /**
     * Create a new node representing a group of histories.  Returns a new HistoryGroup by default.
     *
     * @param parameters Parameters supplied to the action for creating a new group.
     * @return New node representing a history group.
     */
    public HistoryGroup makeGroupNode(DSMap parameters) {
        return new HistoryGroup();
    }

    /**
     * Create a new folder for histories. Returns a new HistoryFolder by default.
     *
     * @param parameters Parameters supplied to the action for creating a new folder.
     * @return New node representing a history folder.
     */
    public HistoryFolder makeHistoryFolder(DSMap parameters) {
        return new HistoryFolder();
    }

    /**
     * Creates a new history. Returns a new History by default.  It is not necessary to apply
     * any of the parameters unless doing something custom.
     *
     * @param parameters Parameters supplied to the action for creating a new history.
     * @return New node representing a history.
     */
    public History makeHistoryNode(DSMap parameters) {
        return new History();
    }

    /**
     * Remove the first N records from the series and return the new first timestamp.
     */
    public abstract DSDateTime purge(History history, int count);

    /**
     * Remove all records in the given time range and return the new first timestamp.
     * The start and/or the end timestamp can be null meaning it's a wild card.  The
     * end timestamp is the first excluded time of the time range.
     */
    public abstract DSDateTime purge(History history, DSTimeRange range);

    /**
     * Purge data from the given time range;
     * <p>
     * By default this scans the subtree for histories and purges each individually.  This can be
     * overridden for batching purposes.
     */
    public void purge(DSNode node, DSTimeRange range) {
        if (node instanceof History) {
            History h = (History) node;
            DSDateTime dt = purge(h, range);
            h.put(FIRST_TS, dt);
            h.put(RECORD_COUNT, getRecordCount(h));
            return;
        }
        DSInfo<?> info = node.getFirstNodeInfo();
        while (info != null) {
            purge(info.getNode(), range);
            info = info.nextNode();
        }
    }

    /**
     * Write the record.
     *
     * @param history   The history to write.
     * @param timestamp The timestamp of the interval.
     * @param value     The value to write.
     * @param status    The status bits to write.
     */
    public abstract void write(History history,
                               DSDateTime timestamp,
                               DSElement value,
                               DSStatus status);

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Package / Private Methods
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Inner Classes
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

}
