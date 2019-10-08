package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSFlexEnum;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.action.DeleteAction;

public interface HistoryConstants {

    public static final String APPLY_ALIAS = "Apply Alias";
    public static final String APPLY_ALIASES = "Apply Aliases";
    public static final String COUNT = "Count";
    public static final String COV = "COV";
    public static final String DAYS = "Days";
    public static final String DELETE = DeleteAction.DELETE;
    public static final String FIRST_TS = "First Timestamp";
    public static final String FOLDER = "Folder";
    public static final String FORCE_OVERWRITE = "Force Overwrite";
    public static final String HISTORY = "History";
    public static final String HISTORY_GROUP = "History Group";
    public static final String HOURS = "Hours";
    public static final String INTERVAL = "Interval";
    public static final String LAST_TS = "Last Timestamp";
    public static final String MAX_RECORDS = "Max Records";
    public static final String MAX_RECORD_AGE = "Max Record Age";
    public static final String MILLIS = "Millis";
    public static final String MIN_COV_INTERVAL = "Min COV Interval";
    public static final String MINUTES = "Minutes";
    public static final String MODE = "Mode";
    public static final String MONTHS = "Months";
    public static final String NAME = "Name";
    public static final String NODE_ONLY = "Node Only";
    public static final String NODE_AND_DATA = "Node and Data";
    public static final String OFF = "Off";
    public static final String ON = "On";
    public static final String PURGE = "Purge";
    public static final String SECONDS = "Seconds";
    public static final String SET = "Set";
    public static final String RECORD_COUNT = "Record Count";
    public static final String TIME_RANGE = "Time Range";
    public static final String TOTALIZED = "Totalized";
    public static final String VALUE = "Value";
    public static final String WATCH_PATH = "Watch Path";
    public static final String WATCH_STATUS = "Watch Status";
    public static final String WATCH_TS = "Watch Timestamp";
    public static final String WATCH_TYPE = "Watch Type";
    public static final String WATCH_VALUE = "Watch Value";
    public static final String WEEKS = "Weeks";
    public static final String UNITS = "Units";
    public static final String TIMEZONE = "Timezone";

    public static final DSFlexEnum DELETE_MODE = DSFlexEnum.valueOf(NODE_ONLY, DSList.valueOf(
            NODE_ONLY, NODE_AND_DATA));

}
