package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSNode;
import org.iot.dsa.time.DSTimeRange;

/**
 * Used to purge the different types of nodes.
 *
 * @author Aaron Hansen
 */
public interface HistoryNode {

    default void houseKeeping() {
    }

    /**
     * Delete data from the given time range.
     */
    default void purge(DSTimeRange range) {
        DSNode node = toNode();
        HistoryUtils.getProvider(node.getInfo()).purge(node, range);
    }

    DSNode toNode();

}
