package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSAction.Parameterless;
import org.iot.dsa.time.DSTimeRange;

class HistoryUtils implements HistoryConstants {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    public static DSAction deleteNodeData = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            String arg = invocation.getParameters().getString(DELETE);
            if (NODE_AND_DATA.equals(arg)) {
                HistoryNode historyNode = (HistoryNode) target.get();
                historyNode.purge(DSTimeRange.NULL);
            }
            target.getParent().remove(target);
            return null;
        }

        {
            setActionGroup(DSAction.EDIT_GROUP);
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
            setActionGroup(DSAction.NEW_GROUP);
            addParameter(NAME, DSString.NULL, "Node name");
        }
    };

    public static DSAction newHistory = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            HistoryProvider provider = HistoryUtils.getProvider(target);
            DSMap params = invocation.getParameters();
            String name = params.getString(NAME);
            String path = params.getString(WATCH_PATH);
            if ((path == null) || path.isEmpty()) {
                throw new IllegalArgumentException("Invalid path: " + path);
            }
            if (name == null) {
                name = path;
            }
            DSNode targetNode = target.getNode();
            if (targetNode.getInfo(name) != null) {
                throw new IllegalArgumentException("Name already in use: " + name);
            }
            History his = provider.makeHistoryNode(invocation.getParameters());
            his.setWatchPath(path);
            target.getNode().add(name, his);
            return null;
        }

        {
            setActionGroup(DSAction.NEW_GROUP);
            addParameter(NAME, DSString.NULL, "Node name").setPlaceHolder("Optional");
            addParameter(WATCH_PATH, DSString.NULL, "Subscription path").setPlaceHolder("Required");
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
            setActionGroup(DSAction.NEW_GROUP);
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
            setActionGroup(DSAction.NEW_GROUP);
            addParameter(NAME, DSString.NULL, "Node name");
        }
    };

    public static DSAction purge = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            HistoryNode node = (HistoryNode) target.get();
            DSTimeRange timeRange = DSTimeRange
                    .valueOf(invocation.getParameters().getString(TIME_RANGE));
            node.purge(timeRange);
            return null;
        }

        {
            addParameter(TIME_RANGE, DSTimeRange.NULL,
                         "Delete records with timestamps in this range");
        }
    };

    public static DSAction writeAliases = new Parameterless() {
        @Override
        public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
            AbstractHistoryNode node = (AbstractHistoryNode) target.get();
            boolean force = invocation.getParameters().get(FORCE_OVERWRITE, false);
            node.writeAliases(force);
            return null;
        }

        {
            addDefaultParameter(FORCE_OVERWRITE, DSBool.FALSE,
                                "Overwrite existing aliases to other nodes");
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
