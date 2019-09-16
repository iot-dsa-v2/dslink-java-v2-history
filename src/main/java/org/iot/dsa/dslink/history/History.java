package org.iot.dsa.dslink.history;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.iot.dsa.dslink.Action.ResultsType;
import org.iot.dsa.dslink.ActionResults;
import org.iot.dsa.dslink.DSIRequester;
import org.iot.dsa.dslink.DSLink;
import org.iot.dsa.dslink.DSLinkConnection;
import org.iot.dsa.dslink.requester.AbstractListHandler;
import org.iot.dsa.dslink.requester.AbstractSubscribeHandler;
import org.iot.dsa.dslink.requester.ErrorType;
import org.iot.dsa.dslink.requester.SimpleRequestHandler;
import org.iot.dsa.node.DSBool;
import org.iot.dsa.node.DSBytes;
import org.iot.dsa.node.DSDouble;
import org.iot.dsa.node.DSElement;
import org.iot.dsa.node.DSElementType;
import org.iot.dsa.node.DSIValue;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSInt;
import org.iot.dsa.node.DSList;
import org.iot.dsa.node.DSLong;
import org.iot.dsa.node.DSMap;
import org.iot.dsa.node.DSNode;
import org.iot.dsa.node.DSPath;
import org.iot.dsa.node.DSStatus;
import org.iot.dsa.node.DSString;
import org.iot.dsa.node.action.DSAction;
import org.iot.dsa.node.action.DSIActionRequest;
import org.iot.dsa.node.event.DSEvent;
import org.iot.dsa.node.event.DSISubscriber;
import org.iot.dsa.node.event.DSISubscription;
import org.iot.dsa.rollup.DSRollup;
import org.iot.dsa.table.DSDeltaTrend;
import org.iot.dsa.table.DSITrend;
import org.iot.dsa.time.DSDateTime;
import org.iot.dsa.time.DSTimeRange;
import org.iot.dsa.time.DSTimezone;
import org.iot.dsa.time.Time;
import org.iot.dsa.util.DSException;

public class History extends AbstractHistoryNode {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    public static final String NEW_RECORD = "NEW_RECORD";
    public static final DSEvent NEW_RECORD_EVENT = new DSEvent(NEW_RECORD);

    private static final GetHistoryAction GET_HISTORY_ACTION = new GetHistoryAction();

    static final String GET_HISTORY = "getHistory";
    static final String GET_HISTORY_ALIAS = "@@getHistory";
    static final String INTERVAL = "Interval";
    static final String REAL_TIME = "Real Time";
    static final String ROLLUP = "Rollup";
    static final String STATUS = "status";
    static final String TIMESTAMP = "timestamp";
    static final String TIMERANGE = "Timerange";
    static final String VALUE = "value";

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private DSDateTime firstTs;
    private HistoryGroup group;
    private DSDateTime lastTs;
    private HistoryProvider provider;
    private DSInfo recordCount = getInfo(RECORD_COUNT);
    private MySubscription subscription;
    private DSElementType type;
    private DSInfo watchSts = getInfo(WATCH_STATUS);
    private DSInfo watchTs = getInfo(WATCH_TS);
    private DSInfo watchVal = getInfo(WATCH_VALUE);

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    public DSDateTime getLastWrite() {
        if (lastTs == null) {
            return DSDateTime.NULL;
        }
        return lastTs;
    }

    public HistoryProvider getProvider() {
        if (provider == null) {
            provider = HistoryUtils.getProvider(getInfo());
        }
        return provider;
    }

    public TimeZone getTimeZone() { //TODO
        return TimeZone.getDefault();
    }

    @Override
    public DSInfo getVirtualAction(DSInfo target, String name) {
        switch (name) {
            case APPLY_ALIAS:
                return virtualInfo(APPLY_ALIAS, HistoryUtils.writeAliases);
            case DELETE:
                return virtualInfo(DELETE, HistoryUtils.deleteNodeData);
            case GET_HISTORY:
                return virtualInfo(GET_HISTORY, GET_HISTORY_ACTION);
        }
        return super.getVirtualAction(target, name);
    }

    @Override
    public void getVirtualActions(DSInfo target, Collection<String> names) {
        super.getVirtualActions(target, names);
        if (target.get() == this) {
            names.add(APPLY_ALIAS);
            names.add(GET_HISTORY);
        }
    }

    @Override
    public void houseKeeping() {
        try {
            if ((subscription == null) && isRunning() && isEnabled()) {
                subscribe();
            }
            boolean modified = false;
            int max = getGroup().getMaxRecords().toInt();
            if (max > 0) {
                int count = getElement(RECORD_COUNT).toInt();
                if (count > max) {
                    modified = true;
                    DSDateTime first = getProvider().purge(this, (count - max));
                    if (first != null) {
                        firstTs = first;
                        put(FIRST_TS, first);
                    }
                }
            }
            HistoryAge historyAge = getGroup().getMaxRecordAge();
            if (!historyAge.isOff()) {
                DSDateTime first = (DSDateTime) get(FIRST_TS);
                if (!first.isNull()) {
                    DSDateTime oldest = historyAge.fromNow(getTimeZone());
                    if (oldest.isAfter(first)) {
                        modified = true;
                        first = getProvider().purge(this,
                                                    DSTimeRange.valueOf(DSDateTime.NULL, oldest));
                        if (first != null) {
                            firstTs = first;
                            put(FIRST_TS, first);
                        }
                    }
                }
            }
            if (modified && isSubscribed()) {
                put(RECORD_COUNT, getProvider().getRecordCount(this));
            }
        } catch (Exception x) {
            error("", x);
        }
    }

    public boolean isTotalized() {
        return getElement(TOTALIZED).toBoolean();
    }

    /**
     * Writes the alias.
     */
    @Override
    public void writeAliases(boolean force) {
        if (force) {
            writeAlias();
        } else {
            writeAliasSafe();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void declareDefaults() {
        super.declareDefaults();
        declareDefault(WATCH_PATH, DSString.EMPTY, "Path of subscription")
                .setReadOnly(true);
        declareDefault(WATCH_TYPE, HistoryType.UNKNOWN, "Value type of subscribed value")
                .setReadOnly(true);
        declareDefault(WATCH_STATUS, DSStatus.unknown, "Status of the subscribed value")
                .setReadOnly(true)
                .setTransient(true);
        put(WATCH_VALUE, DSBool.FALSE)
                .setReadOnly(true)
                .setTransient(true)
                .setLocked(true)
                .getMetadata().setDescription("Last subscription update");
        declareDefault(WATCH_TS, DSDateTime.NULL, "Timestamp of last subscription update")
                .setReadOnly(true)
                .setTransient(true);
        declareDefault(FIRST_TS, DSDateTime.NULL, "Timestamp of first record in the database")
                .setReadOnly(true);
        declareDefault(LAST_TS, DSDateTime.NULL, "Timestamp of last record in the database")
                .setReadOnly(true);
        put(RECORD_COUNT, DSLong.NULL)
                .setReadOnly(true)
                .setLocked(true)
                .getMetadata().setDescription("Number of record in the database");
        declareDefault(TOTALIZED, DSBool.FALSE,
                       "True means the value represents an accumulation (ever increasing slope)");
        declareDefault(TIMEZONE, DSTimezone.DEFAULT, "Timezone of the data source.");
        //TODO UNITS
    }

    /**
     * The path to the getHistory action of this node.
     */
    protected String getGetHistoryPath() {
        DSLink link = (DSLink) getAncestor(DSLink.class);
        String myPath = link.getPathInBroker(this);
        return DSPath.concat(myPath, GET_HISTORY, null).toString();
    }

    protected HistoryGroup getGroup() {
        if (group == null) {
            group = HistoryUtils.getGroup(getInfo());
        }
        return group;
    }

    protected DSITrend getHistory(DSIActionRequest request) {
        DSMap parameters = request.getParameters();
        String param = parameters.getString(TIMERANGE);
        DSTimeRange range = DSTimeRange.valueOf(param);
        if ((range == null) || range.isNull()) {
            //default to today
            DSDateTime to = DSDateTime.valueOf(System.currentTimeMillis(), getTimeZone());
            range = DSTimeRange.valueOf(to.startOfDay(), to);
        }
        param = parameters.getString(ROLLUP);
        DSRollup rollup = DSRollup.FIRST;
        boolean delta = false;
        if (param != null) {
            if (param.equals("Delta")) {
                delta = true;
            } else if (!param.isEmpty()) {
                rollup = DSRollup.valueFor(param);
            }
        }
        GetHistoryInterval interval = GetHistoryInterval.valueOf(parameters.getString(INTERVAL));
        if (interval != null) {
            DSDateTime start = range.getStart();
            Calendar cal = Time.getCalendar(start.timeInMillis(), getTimeZone());
            if (interval.align(cal)) {
                start = DSDateTime.valueOf(cal);
                range = DSTimeRange.valueOf(start, range.getTo());
            }
        }
        DSITrend trend = null;
        boolean cov = getGroup().isCov();
        if (cov) {
            trend = getProvider().getTrendCov(this, range.getStart());
        } else {
            trend = getProvider().getTrend(this, range.getStart());
        }
        if (interval != null) {
            if (delta || isTotalized()) {
                trend = new DSDeltaTrend(trend);
            }
            trend = new GetHistoryIntervalTrend(trend, interval, rollup, cov, getTimeZone());
        } else if (delta) {
            trend = new DSDeltaTrend(trend);
        }
        return new EndTrend(trend, range.getEnd());
    }

    protected String getWatchPath() {
        return get(WATCH_PATH).toString();
    }

    protected DSStatus getWatchStatus() {
        return (DSStatus) watchSts.get();
    }

    protected DSElement getWatchValue() {
        return watchVal.getElement();
    }

    @Override
    protected void onChildChanged(DSInfo info) {
        if (info == status) {
            if (!getStatus().isGood()) {
                lastTs = null;
            } else if (lastTs == null) {
                writeStart();
            }
        }
        super.onChildChanged(info);
    }

    @Override
    protected void onRemoved() {
        super.onRemoved();
        removeAlias();
    }

    @Override
    protected void onRenamed(String oldName) {
        super.onRenamed(oldName);
        String oldPath = DSPath.concat(getParent().getPath(), oldName, null).toString();
        replaceAlias(oldPath);
    }

    @Override
    protected void onStable() {
        super.onStable();
        updateFirstAndCount();
        subscribe();
    }

    @Override
    protected void onStarted() {
        group = null;
        getProvider().init(this);
        super.onStarted();
    }

    @Override
    protected void onStopped() {
        lastTs = null;
        unsubscribe();
        super.onStopped();
    }

    protected void removeAlias() {
        final DSLink link = (DSLink) getAncestor(DSLink.class);
        final DSIRequester requester = link.getConnection().getRequester();
        requester.list(getWatchPath(), new AbstractListHandler() {
            @Override
            public void onClose() {
            }

            @Override
            public void onError(ErrorType type, String msg) {
                error(String.format("%s %s %s", getPath(), type.name(), msg));
            }

            @Override
            public void onInitialized() {
                getStream().closeStream();
            }

            @Override
            public void onRemove(String name) {
            }

            @Override
            public void onUpdate(String name, DSElement value) {
                if (name.equals(GET_HISTORY_ALIAS)) {
                    final String path = getGetHistoryPath();
                    if (value.isList()) {
                        DSList list = value.toList();
                        DSString dspath = DSString.valueOf(path);
                        if (list.remove(dspath)) {
                            if (list.isEmpty()) {
                                requester.remove(path, new SimpleRequestHandler());
                            } else {
                                writeAlias(list);
                            }
                        }
                    }
                }
            }
        });
    }

    protected void replaceAlias(final String oldPath) {
        final DSLink link = (DSLink) getAncestor(DSLink.class);
        final DSIRequester requester = link.getConnection().getRequester();
        requester.list(getWatchPath(), new AbstractListHandler() {
            @Override
            public void onClose() {
            }

            @Override
            public void onError(ErrorType type, String msg) {
                error(String.format("%s %s %s", getPath(), type.name(), msg));
            }

            @Override
            public void onInitialized() {
                getStream().closeStream();
            }

            @Override
            public void onRemove(String name) {
            }

            @Override
            public void onUpdate(String name, DSElement value) {
                if (name.equals(GET_HISTORY_ALIAS)) {
                    final String path = getGetHistoryPath();
                    if (value.isList()) {
                        DSList list = value.toList();
                        int idx = list.indexOf(oldPath);
                        if (idx >= 0) {
                            DSString dspath = DSString.valueOf(path);
                            list.put(idx, DSString.valueOf(path));
                            writeAlias(list);
                        }
                    }
                }
            }
        });
    }

    protected void setWatchPath(String path) {
        put(WATCH_PATH, DSString.valueOf(path));
    }

    protected void subscribe() {
        DSLink link = (DSLink) getAncestor(DSLink.class);
        DSLinkConnection conn = link.getConnection();
        if (conn.isConnected()) {
            subscription = (MySubscription) conn.getRequester().subscribe(getWatchPath(),
                                                                          DSInt.valueOf(0),
                                                                          new MySubscription());
        }
    }

    protected void unsubscribe() {
        if (subscription != null) {
            subscription.getStream().closeStream();
            subscription = null;
        }
    }

    protected void updateFirstAndCount() {
        firstTs = getProvider().getFirstTimestamp(this);
        put(FIRST_TS, firstTs);
        put(RECORD_COUNT, getProvider().getRecordCount(this));
    }

    /**
     * Captures type if needed, sets last timestamp and calls write on the provider.  Overrides
     * should call super.
     */
    protected void write(DSDateTime ts, DSElement value, DSStatus status) {
        if (!isEnabled()) {
            return;
        }
        if (type == null) {
            DSInfo info = getInfo(WATCH_TYPE);
            HistoryType htype = (HistoryType) info.get();
            if (htype.isUnknown()) {
                htype = HistoryType.valueFor(value);
                put(info, htype);
            }
            if (!htype.isUnknown()) {
                type = value.getElementType();
            }
            if (type == null) {
                return;
            }
        }
        lastTs = ts;
        if (type != value.getElementType()) {
            switch (type) {
                case BOOLEAN:
                    value = DSBool.NULL.valueOf(value);
                    break;
                case BYTES:
                    value = DSBytes.NULL.valueOf(value);
                    break;
                case DOUBLE:
                    value = DSDouble.NULL.valueOf(value);
                    break;
                //case LIST: //TODO
                //value = DSList.NULL.valueOf(value);
                //break;
                case LONG:
                    break;
                //case MAP: //TODO
                //value = DSMap.NULL.valueOf(value);
                //break;
                case STRING:
                    value = DSString.NULL.valueOf(value);
                    break;
            }
        }
        provider.write(this, ts, value, status);
        put(LAST_TS, ts);
        if ((firstTs == null) || firstTs.isNull()) {
            firstTs = ts;
            put(FIRST_TS, ts);
        }
        put(RECORD_COUNT, DSLong.valueOf(recordCount.getElement().toLong() + 1));
        fire(NEW_RECORD_EVENT, null, null);
    }

    /**
     * Submits a set request to the requester.
     */
    protected void writeAlias() {
        writeAlias(new DSList().add(getGetHistoryPath()));
    }

    /**
     * Submits a set request to the requester.
     */
    protected void writeAlias(DSList pathList) {
        String aliasPath = DSPath.concat(getWatchPath(), GET_HISTORY_ALIAS, null).toString();
        DSLink link = (DSLink) getAncestor(DSLink.class);
        DSIRequester requester = link.getConnection().getRequester();
        requester.set(aliasPath, pathList, new SimpleRequestHandler() {
            @Override
            public void onError(ErrorType type, String msg) {
                error(String.format("%s %s %s", getGetHistoryPath(), type.name(), msg));
            }
        });
    }

    /**
     * Will only write the alias if the watch path doesn't already have one.
     */
    protected void writeAliasSafe() {
        DSLink link = (DSLink) getAncestor(DSLink.class);
        DSIRequester requester = link.getConnection().getRequester();
        requester.list(getWatchPath(), new AbstractListHandler() {
            boolean safe = true;

            @Override
            public void onClose() {
                if (safe) {
                    writeAlias();
                }
            }

            @Override
            public void onError(ErrorType type, String msg) {
                safe = false;
                error(String.format("%s %s %s", getPath(), type.name(), msg));
            }

            @Override
            public void onInitialized() {
                getStream().closeStream();
            }

            @Override
            public void onRemove(String name) {
            }

            @Override
            public void onUpdate(String name, DSElement value) {
                if (name.equals(GET_HISTORY_ALIAS)) {
                    safe = false;
                    String path = getGetHistoryPath();
                    if (value.isList()) {
                        DSList list = value.toList();
                        DSString dspath = DSString.valueOf(path);
                        if (!list.contains(dspath)) {
                            writeAlias(list.add(dspath));
                        }
                    }
                }
            }
        });
    }

    /**
     * Only calls write if the group is cov or if the start status flag needs to be written.
     */
    protected void writeCov(DSDateTime ts, DSElement val, DSStatus status) {
        if ((lastTs == null) && status.isGood()) {
            status = status.add(DSStatus.START);
        } else if (!getGroup().canWriteCov(History.this)) {
            return;
        }
        write(ts, val, status);
    }

    /**
     * Called by the group for interval collection.  This calls write(time,val,sts) unless
     * it does not have a current value.
     *
     * @param timestamp Use this timestamp when writing, it will be aligned properly.
     */
    protected void writeInterval(DSDateTime timestamp) {
        if (lastTs == null) {
            return;
        }
        write(timestamp, getWatchValue(), getWatchStatus());
    }

    /**
     * Called when the start status flag is needed.  This calls write(time,val,sts) unless
     * it does not have a current value.
     */
    protected void writeStart() {
        if ((lastTs == null) || getGroup().isCov()) {
            return;
        }
        DSStatus status = getWatchStatus().add(DSStatus.START);
        write(DSDateTime.now(), getWatchValue(), status);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Package Methods
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Inner Classes
    ///////////////////////////////////////////////////////////////////////////

    protected static class GetHistory implements ActionResults, DSISubscriber {

        private History history;
        private boolean realTime;
        private DSIActionRequest request;
        private DSISubscription subscription;
        private DSITrend trend;
        private List<DSList> updates = null;

        protected GetHistory(History history, DSIActionRequest request) {
            this.history = history;
            this.request = request;
            this.trend = history.getHistory(request);
            realTime = request.getParameters().get(REAL_TIME, false);
        }

        @Override
        public int getColumnCount() {
            return trend.getColumnCount();
        }

        @Override
        public void getColumnMetadata(int index, DSMap bucket) {
            trend.getColumnMetadata(index, bucket);
        }

        @Override
        public void getResults(DSList row) {
            if (trend != null) {
                for (int i = 0, len = getColumnCount(); i < len; i++) {
                    row.add(trend.getValue(i).toElement());
                }
            } else {
                synchronized (this) {
                    DSList list = updates.remove(0);
                    row.addAll(list);
                }
            }
        }

        @Override
        public ResultsType getResultsType() {
            return ResultsType.TABLE;
        }

        @Override
        public boolean next() {
            boolean ret = false;
            if (trend != null) {
                ret = trend.next();
                if (ret == false) {
                    if (realTime) {
                        subscription = history.subscribe(this, NEW_RECORD_EVENT, null);
                        trend = null;
                    } else {
                        request.close();
                    }
                }
            }
            if (!ret && realTime) {
                if (updates != null) {
                    ret = updates.size() > 0;
                }
            }
            return ret;
        }

        @Override
        public void onClose() {
            if (subscription != null) {
                subscription.close();
                subscription = null;
            }
        }

        @Override
        public synchronized void onEvent(DSEvent event, DSNode node, DSInfo child, DSIValue data) {
            if (updates == null) {
                updates = new LinkedList<>();
            }
            updates.add(new DSList().add(history.getLastWrite().toElement())
                                    .add(history.getWatchValue())
                                    .add(history.getWatchStatus().toElement()));
            request.enqueueResults();
        }

    }

    private static class GetHistoryAction extends DSAction {

        public GetHistoryAction() {
        }

        @Override
        public ActionResults invoke(DSIActionRequest request) {
            History h = (History) request.getTarget();
            return new GetHistory(h, request);
        }

        @Override
        public void prepareParameter(DSInfo target, DSMap parameter) {
        }

        {
            addParameter(TIMERANGE, DSTimeRange.NULL, null);
            addDefaultParameter(INTERVAL, DSString.valueOf("none"), null);
            addDefaultParameter(ROLLUP, DSRollup.FIRST, null);
            addDefaultParameter(REAL_TIME, DSBool.FALSE, null);
            setResultsType(ResultsType.TABLE);
        }
    }

    private class MySubscription extends AbstractSubscribeHandler {

        @Override
        public void onClose() {
            subscription = null;
        }

        @Override
        public void onError(ErrorType type, String msg) {
            error(String.format("%s %s %s", getPath(), type.name(), msg));
        }

        @Override
        public void onUpdate(DSDateTime dateTime, DSElement value, DSStatus status) {
            try {
                put(watchTs, dateTime);
                put(watchVal, value);
                put(watchSts, status);
                writeCov(dateTime, value, status);
            } catch (Exception x) {
                error(getPath(), x);
                updateStatus(DSException.makeMessage(x));
            }
        }

        {
            lastTs = null;
        }
    }

}
