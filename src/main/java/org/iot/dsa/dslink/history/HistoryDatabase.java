package org.iot.dsa.dslink.history;

import java.util.Collection;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.conn.DSConnection;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

public class HistoryDatabase extends DSConnection implements HistoryConstants, HistoryNode {

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
    public DSInfo getVirtualAction(DSInfo target, String name) {
        if (target.get() == this) {
            switch (name) {
                case DELETE:
                    return actionInfo(DELETE, HistoryUtils.deleteNodeData);
                case FOLDER:
                    return actionInfo(FOLDER, HistoryUtils.newGroupFolder);
                case HISTORY_GROUP:
                    return actionInfo(HISTORY_GROUP, HistoryUtils.newHistoryGroup);
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

    @Override
    protected void checkConfig() {
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
    }

    @Override
    protected void doConnect() {
    }

    @Override
    protected void doDisconnect() {
    }

    @Override
    protected void doPing() {
    }

    /**
     * Has DSRuntime execute the inherited run method.
     */
    @Override
    protected void onStable() {
        DSRuntime.run(this);
        super.onStable();
    }

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
