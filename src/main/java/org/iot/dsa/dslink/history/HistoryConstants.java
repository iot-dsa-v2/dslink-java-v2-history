package org.iot.dsa.dslink.history;

import org.iot.dsa.node.DSFlexEnum;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.action.DeleteAction;

/**
 * Constants used by multiple classes.
 *
 * @author Aaron Hansen
 */
public interface HistoryConstants {

    String APPLY_ALIAS = "Apply Alias";
    String APPLY_ALIASES = "Apply Aliases";
    String COUNT = "Count";
    String COV = "COV";
    String DAYS = "Days";
    String DELETE = DeleteAction.DELETE;
    String FIRST_TS = "First Timestamp";
    String FOLDER = "Folder";
    String FORCE_OVERWRITE = "Force Overwrite";
    String HISTORY = "History";
    String HISTORY_GROUP = "History Group";
    String HOURS = "Hours";
    String INTERVAL = "Interval";
    String LAST_TS = "Last Timestamp";
    String MAX_RECORDS = "Max Records";
    String MAX_RECORD_AGE = "Max Record Age";
    String MILLIS = "Millis";
    String MIN_COV_INTERVAL = "Min COV Interval";
    String MINUTES = "Minutes";
    String MODE = "Mode";
    String MONTHS = "Months";
    String NAME = "Name";
    String NODE_ONLY = "Node Only";
    String NODE_AND_DATA = "Node and Data";
    String OFF = "Off";
    String ON = "On";
    String PURGE = "Purge";
    String SECONDS = "Seconds";
    String SET = "Set";
    String RECORD_COUNT = "Record Count";
    String TIME_RANGE = "Time Range";
    String TOTALIZED = "Totalized";
    String VALUE = "Value";
    String WATCH_PATH = "Watch Path";
    String WATCH_STATUS = "Watch Status";
    String WATCH_TS = "Watch Timestamp";
    String WATCH_TYPE = "Watch Type";
    String WATCH_VALUE = "Watch Value";
    String WEEKS = "Weeks";
    String UNITS = "Units";
    String TIMEZONE = "Timezone";

    DSFlexEnum DELETE_MODE = DSFlexEnum.valueOf(NODE_ONLY, DSList.valueOf(
            NODE_ONLY, NODE_AND_DATA));

}
