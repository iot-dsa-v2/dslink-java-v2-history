package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSInfo;

import java.util.Collection;

/**
 * Contains histories and other history folders.
 *
 * @author Aaron Hansen
 */
public class HistoryFolder extends AbstractHistoryNode {

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
            switch (name) {
                case APPLY_ALIASES:
                    return virtualInfo(APPLY_ALIASES, HistoryUtils.writeAliases);
                case DELETE:
                    return virtualInfo(DELETE, HistoryUtils.deleteNodeData);
                case FOLDER:
                    return virtualInfo(FOLDER, HistoryUtils.newHistoryFolder);
                case HISTORY:
                    return virtualInfo(HISTORY, HistoryUtils.newHistory);
            }
        }
        return super.getVirtualAction(target, name);
    }

    @Override
    public void getVirtualActions(DSInfo<?> target, Collection<String> names) {
        super.getVirtualActions(target, names);
        if (target.get() == this) {
            names.add(FOLDER);
            names.add(HISTORY);
            names.add(APPLY_ALIASES);
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
