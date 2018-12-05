package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSNode;

/**
 * Used to purge the different types of nodes.
 *
 * @author Aaron Hansen
 */
public interface HistoryPurge {

    /**
     * Purge data from the exclusive start time to the exclusive end time.  Negative values
     * can be used as wild cards in both the start and end.
     *
     * @param start Inclusive start, or &lt;0 for everything up until the end.
     * @param end   Exclusive end, or &lt;0 for everything after the start.
     */
    public void purge(long start, long end);

    public DSNode toNode();

}
