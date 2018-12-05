package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSAction.Parameterless;

public class HistoryUtils implements HistoryContants {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    public static DSAction deleteNodeData = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            String arg = invocation.getParameters().getString(DELETE);
            if (NODE_AND_DATA.equals(arg)) {
                HistoryPurge historyPurge = (HistoryPurge) target.get();
                historyPurge.purge(-1, -1);
            }
            target.getParent().remove(target);
            return null;
        }

        {
            //setActionGroup(DSAction.EDIT_GROUP); //TODO
            addDefaultParameter(DELETE, DELETE_MODE,
                                "Delete only the node or the node and all backing data");
        }
    };

    public static DSAction newGroupFolder = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            HistoryProvider provider = HistoryUtils.getProvider(target);
            target.getNode().add(
                    invocation.getParameters().getString(NAME),
                    provider.makeGroupFolder(invocation.getParameters()));
            return null;
        }

        {
            //setActionGroup(DSAction.NEW_GROUP); //TODO
            addParameter(NAME, DSString.NULL, "Node name");
        }
    };

    public static DSAction newHistoryFolder = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            HistoryProvider provider = HistoryUtils.getProvider(target);
            target.getNode().add(
                    invocation.getParameters().getString(NAME),
                    provider.makeHistoryFolder(invocation.getParameters()));
            return null;
        }

        {
            //setActionGroup(DSAction.NEW_GROUP); //TODO
            addParameter(NAME, DSString.NULL, "Node name");
        }
    };

    public static DSAction newHistoryGroup = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            HistoryProvider provider = HistoryUtils.getProvider(target);
            target.getNode().add(
                    invocation.getParameters().getString(NAME),
                    provider.makeGroupNode(invocation.getParameters()));
            return null;
        }

        {
            //setActionGroup(DSAction.NEW_GROUP); //TODO
            addParameter(NAME, DSString.NULL, "Node name");
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    private HistoryUtils() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    public static HistoryDatabase getDatabase(DSInfo target) {
        DSNode node = target.getParent();
        if (target.isNode()) {
            node = target.getNode();
        }
        while (node != null) {
            if (node instanceof HistoryDatabase) {
                return (HistoryDatabase) node;
            }
            node = node.getParent();
        }
        return null;
    }

    public static HistoryGroup getGroup(DSInfo target) {
        DSNode node = target.getParent();
        if (target.isNode()) {
            node = target.getNode();
        }
        while (node != null) {
            if (node instanceof HistoryGroup) {
                return (HistoryGroup) node;
            }
            node = node.getParent();
        }
        return null;
    }

    public static HistoryMainNode getMain(DSInfo target) {
        DSNode node = target.getParent();
        if (target.isNode()) {
            node = target.getNode();
        }
        while (node != null) {
            if (node instanceof HistoryMainNode) {
                return (HistoryMainNode) node;
            }
            node = node.getParent();
        }
        return null;
    }

    public static HistoryProvider getProvider(DSInfo target) {
        HistoryMainNode node = getMain(target);
        if (node == null) {
            return null;
        }
        return node.getProvider();
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
