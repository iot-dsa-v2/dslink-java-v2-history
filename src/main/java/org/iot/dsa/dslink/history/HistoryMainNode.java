package org.iot.dsa.dslink.history;

import java.util.Collection;
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
public abstract class HistoryMainNode extends DSMainNode {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    public static final String NEW_DATABASE = "Database";
    public static final String NAME = "Name";

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
    public DSInfo getDynamicAction(DSInfo target, String name) {
        if (target.get() == this) {
            if (NEW_DATABASE.equals(name)) {
                return makeNewDatabaseAction();
            }
        }
        return super.getDynamicAction(target, name);
    }

    @Override
    public void getDynamicActions(DSInfo target, Collection<String> bucket) {
        if (target.get() == this) {
            bucket.add(NEW_DATABASE);
        }
        super.getDynamicActions(target, bucket);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    protected abstract HistoryProvider getProvider();

    /**
     * Override point.  By default this creates an action with a single name parameter and
     * uses getProvider().makeDatabaseNode(actionParameters).
     * @return
     */
    protected DSInfo makeNewDatabaseAction() {
        DSInfo ret = actionInfo(NEW_DATABASE, new DSAction.Parameterless() {
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

}
