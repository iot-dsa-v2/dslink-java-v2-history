package org.iot.dsa.dslink.history;

import org.iot.dsa.conn.DSConnection;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

import java.util.Collection;

/**
 * Abstract representation of a connection(s) to a database.
 *
 * @author Aaron Hansen
 */
public abstract class HistoryDatabase extends DSConnection implements HistoryConstants,
        HistoryNode {

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DSInfo<?> getVirtualAction(DSInfo<?> target, String name) {
        if (target.get() == this) {
            switch (name) {
                case DELETE:
                    return virtualInfo(DELETE, HistoryUtils.deleteNodeData);
                case FOLDER:
                    return virtualInfo(FOLDER, HistoryUtils.newGroupFolder);
                case HISTORY_GROUP:
                    return virtualInfo(HISTORY_GROUP, HistoryUtils.newHistoryGroup);
            }
        }
        return super.getVirtualAction(target, name);
    }

    @Override
    public void getVirtualActions(DSInfo<?> target, Collection<String> names) {
        super.getVirtualActions(target, names);
        if (target.get() == this) {
            names.add(HISTORY_GROUP);
            names.add(FOLDER);
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

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

}
