package org.iot.dsa.dslink.history;

import java.util.Collection;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.dslink.DSMainNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.ActionInvocation;
import org.iot.dsa.node.action.ActionResult;
import org.iot.dsa.node.action.DSAction;

/**
 * The main and only node of this link.
 *
 * @author Aaron Hansen
 */
public abstract class HistoryMainNode extends DSMainNode implements HistoryConstants {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    public static final String NEW_DATABASE = "Database";
    public static final String NAME = "Name";

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSRuntime.Timer houseKeepingTimer;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DSInfo getVirtualAction(DSInfo target, String name) {
        if (target.get() == this) {
            if (NEW_DATABASE.equals(name)) {
                return makeNewDatabaseAction();
            }
        }
        return super.getVirtualAction(target, name);
    }

    @Override
    public void getVirtualActions(DSInfo target, Collection<String> bucket) {
        if (target.get() == this) {
            bucket.add(NEW_DATABASE);
        }
        super.getVirtualActions(target, bucket);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Continuously scans the entire tree and calls houseKeeping on all HistoryNodes.
     */
    protected void doHousekeeping() {
        HistoryNode hnode;
        while (isRunning()) {
            DSInfo info = getFirstInfo(HistoryNode.class);
            while (info != null) {
                hnode = (HistoryNode) info.get();
                hnode.houseKeeping();
                Thread.yield();
                info = info.next(HistoryNode.class);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException x) {
                debug(getPath(), x);
            }
        }
    }

    protected abstract HistoryProvider getProvider();

    /**
     * Override point.  By default this creates an action with a single name parameter and
     * uses getProvider().makeDatabaseNode(actionParameters).
     */
    protected DSInfo makeNewDatabaseAction() {
        DSInfo ret = virtualInfo(NEW_DATABASE, new DSAction.Parameterless() {
            @Override
            public ActionResult invoke(DSInfo target, ActionInvocation invocation) {
                String name = invocation.getParameters().getString(NAME);
                DSNode dbnode = getProvider().makeDatabaseNode(invocation.getParameters());
                add(name, dbnode);
                return null;
            }

            {
                addParameter(NAME, DSString.NULL, "The node name");
            }
        });
        ret.getMetadata().setActionGroup(DSAction.NEW_GROUP, null);
        return ret;
    }

    /**
     * Asynchronously runs housekeeping.
     */
    @Override
    protected void onStable() {
        super.onStable();
        DSRuntime.run(() -> {
            doHousekeeping();
        });
    }

    @Override
    protected void onStopped() {
        if (houseKeepingTimer != null) {
            houseKeepingTimer.cancel();
            houseKeepingTimer = null;
        }
        super.onStopped();
    }


}
