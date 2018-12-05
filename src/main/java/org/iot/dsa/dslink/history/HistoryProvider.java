package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;

public abstract class HistoryProvider {

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
     * Create a new node representing a group of histories.
     *
     * @param parameters Parameters supplied to the action for creating a new group.
     * @return New node representing a history group.
     */
    public abstract HistoryGroup makeGroupNode(DSMap parameters);

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
     * Purge data from the exclusive start time to the exclusive end time.  Negative values
     * can be used as wild cards.
     * <p>
     * By default scans the subtree for histories and purges each individually.  This can be
     * overridden for batching purposes.
     *
     * @param start Inclusive start, or &lt;0 for everything up until the end.
     * @param end   Exclusive end, or &lt;0 for everything after the start.
     */
    public void purge(DSNode node, long start, long end) {
    }

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
