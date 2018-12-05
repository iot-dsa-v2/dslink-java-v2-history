package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSFlexEnum;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.action.DeleteAction;

public interface HistoryContants {

    public static final String DELETE = DeleteAction.DELETE;
    public static final String FOLDER = "Folder";
    public static final String HISTORY = "History";
    public static final String HISTORY_GROUP = "History Group";
    public static final String NAME = "Name";
    public static final String NODE_ONLY = "Node Only";
    public static final String NODE_AND_DATA = "Node and Data";

    public static final DSFlexEnum DELETE_MODE = DSFlexEnum.valueOf(NODE_ONLY, DSList.valueOf(
            NODE_ONLY, NODE_AND_DATA));

}
