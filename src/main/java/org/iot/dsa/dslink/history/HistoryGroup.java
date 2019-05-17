package org.iot.dsa.dslink.history;

import java.util.Calendar;
import java.util.Collection;
import org.iot.dsa.DSRuntime;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.time.DSDateTime;
import org.iot.dsa.time.DSTime;

public class HistoryGroup extends AbstractHistoryNode {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSInfo cov = getInfo(COV);
    private DSInfo interval = getInfo(INTERVAL);
    private DSInfo maxRecordAge = getInfo(MAX_RECORD_AGE);
    private DSInfo minCovInterval = getInfo(MIN_COV_INTERVAL);
    private HistoryProvider provider;
    private DSRuntime.Timer timer;

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    public HistoryInterval getInterval() {
        return (HistoryInterval) interval.get();
    }

    public HistoryAge getMaxRecordAge() {
        return (HistoryAge) maxRecordAge.get();
    }

    public DSInt getMaxRecords() {
        return (DSInt) get(MAX_RECORDS);
    }

    public HistoryInterval getMinCovInterval() {
        return (HistoryInterval) minCovInterval.get();
    }

    @Override
    public DSInfo getVirtualAction(DSInfo target, String name) {
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
    public void getVirtualActions(DSInfo target, Collection<String> names) {
        super.getVirtualActions(target, names);
        if (target.get() == this) {
            names.add(APPLY_ALIASES);
            names.add(FOLDER);
            names.add(HISTORY);
        }
    }

    public boolean isCov() {
        return cov.getElement().toBoolean();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called by each descendant history on every cov, regardless if cov is enabled or not.  If
     * something should be written, this will call the write method on the history with the
     * values to write.
     */
    protected boolean canWriteCov(History history) {
        if (!isCov()) {
            return false;
        }
        if (!getMinCovInterval().isOff()) {
            long now = System.currentTimeMillis();
            Calendar cal = DSTime.getCalendar(history.getLastWrite().timeInMillis());
            getMinCovInterval().apply(cal);
            long next = cal.getTimeInMillis();
            DSTime.recycle(cal);
            return next <= System.currentTimeMillis();
        }
        return true;
    }

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(INTERVAL, HistoryInterval.NULL, "Collect at a regular interval");
        declareDefault(COV, DSBool.FALSE, "Collect Changes Of Value")
                .getMetadata().setBooleanRange(OFF, ON);
        declareDefault(MIN_COV_INTERVAL, HistoryInterval.valueOf("10 Seconds"),
                       "Ignore changes of value that come too fast");
        declareDefault(MAX_RECORDS, DSInt.valueOf(0),
                       "If greater than zero, histories will be trimmed to this size");
        declareDefault(MAX_RECORD_AGE, HistoryAge.NULL,
                       "Records older than this will be periodically deleted");
    }

    /**
     * Walks the subtree and writes each history.
     */
    protected void executeInterval() {
        long time = System.currentTimeMillis();
        Calendar cal = DSTime.getCalendar(time);
        getInterval().align(cal);
        time = cal.getTimeInMillis();
        DSTime.recycle(cal);
        collectInterval(DSDateTime.valueOf(time), this);
    }

    @Override
    protected void onChildChanged(DSInfo child) {
        if ((child == cov) || (child == interval) || (child == status)) {
            scheduleCollection();
        }
        super.onChildChanged(child);
    }

    @Override
    protected void onStable() {
        super.onStable();
        scheduleCollection();
    }

    /**
     * Cancels an existing interval timer, then schedules another if operational.
     */
    protected void scheduleCollection() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (!isEnabled() || !isRunning()) {
            return;
        }
        HistoryInterval ivl = getInterval();
        if (ivl.isOff()) {
            return;
        }
        long now = System.currentTimeMillis();
        Calendar cal = DSTime.getCalendar(now);
        ivl.align(cal);
        ivl.apply(cal);
        long first = cal.getTimeInMillis();
        timer = DSRuntime.run(() -> executeInterval(), first, ivl.toMillis());
        DSTime.recycle(cal);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private Methods
    ///////////////////////////////////////////////////////////////////////////

    private void collectInterval(DSDateTime timestamp, DSNode node) {
        if (node instanceof History) {
            if (provider == null) {
                provider = HistoryUtils.getProvider(getInfo());
            }
            History h = (History) node;
            h.writeInterval(timestamp);
        } else {
            DSInfo info = node.getFirstNodeInfo();
            while (info != null) {
                collectInterval(timestamp, info.getNode());
                info = info.nextNode();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Inner Classes
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

}
