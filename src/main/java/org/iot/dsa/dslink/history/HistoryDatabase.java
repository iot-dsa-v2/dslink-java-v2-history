package org.iot.dsa.dslink.history;

import java.util.Collection;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.conn.DSConnection;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

public abstract class HistoryDatabase extends DSConnection implements HistoryConstants,
        HistoryNode {

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DSInfo getVirtualAction(DSInfo target, String name) {
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
    public void getVirtualActions(DSInfo target, Collection<String> names) {
        super.getVirtualActions(target, names);
        names.add(HISTORY_GROUP);
        names.add(FOLDER);
    }

    @Override
    public DSNode toNode() {
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Has DSRuntime execute the inherited run method.
     */
    @Override
    protected void onStable() {
        DSRuntime.run(this);
        super.onStable();
    }

}
