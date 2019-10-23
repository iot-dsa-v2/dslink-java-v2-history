package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.util.DSEnabledNode;

import java.util.Collection;

/**
 * Common functions used by the various nodes in the historian architecture.
 *
 * @author Aaron Hansen
 */
public class AbstractHistoryNode extends DSEnabledNode implements HistoryConstants, HistoryNode {

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

    @Override
    public DSInfo<?> getVirtualAction(DSInfo<?> target, String name) {
        if (target.get() == this) {
            if (PURGE.equals(name)) {
                return virtualInfo(PURGE, HistoryUtils.purge);
            }
        }
        return super.getVirtualAction(target, name);
    }

    @Override
    public void getVirtualActions(DSInfo<?> target, Collection<String> names) {
        super.getVirtualActions(target, names);
        if (target.get() == this) {
            names.add(PURGE);
        }
    }

    @Override
    public void houseKeeping() {
        HistoryNode node;
        DSInfo<?> info = getFirstInfo(HistoryNode.class);
        while (info != null) {
            node = (HistoryNode) info.get();
            node.houseKeeping();
            Thread.yield();
            info = info.next(HistoryNode.class);
        }
    }

    @Override
    public DSNode toNode() {
        return this;
    }

    /**
     * Cascades the call down the tree.
     *
     * @param force Overwrite an existing alias to another node.
     */
    public void writeAliases(boolean force) {
        DSInfo<?> info = getFirstNodeInfo();
        while (info != null) {
            if (info.is(AbstractHistoryNode.class)) {
                ((AbstractHistoryNode) info.get()).writeAliases(force);
            }
            info = info.nextNode();
        }
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
