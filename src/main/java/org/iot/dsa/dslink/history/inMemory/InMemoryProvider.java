package org.iot.dsa.dslink.history.inMemory;

import org.iot.dsa.dslink.history.History;
import org.iot.dsa.dslink.history.HistoryDatabase;
import org.iot.dsa.dslink.history.HistoryProvider;
import org.iot.dsa.dslink.history.table.DSITrend;
import org.iot.dsa.node.*;
import org.iot.dsa.time.DSDateTime;
import org.iot.dsa.time.DSTimeRange;

import java.util.HashMap;
import java.util.Map;

/**
 * Provider implementation which simply store data in memory.
 * @author Aaron Hansen
 */
public class InMemoryProvider extends HistoryProvider {

    ///////////////////////////////////////////////////////////////////////////
    // Class Fields
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Instance Fields
    ///////////////////////////////////////////////////////////////////////////

    private Map<History, HistoryImpl> histories = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Public Methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public DSDateTime getFirstTimestamp(History history) {
        HistoryImpl impl = histories.get(history);
        if (impl == null) {
            return DSDateTime.NULL;
        }
        HistoryRecord rec = impl.head;
        if (rec == null) {
            return DSDateTime.NULL;
        }
        return rec.timestamp;
    }

    @Override
    public int getRecordCount(History history) {
        HistoryImpl impl = histories.get(history);
        if (impl == null) {
            return 0;
        }
        return impl.size;
    }

    @Override
    public DSITrend getTrend(History history, DSDateTime start) {
        return new InMemoryTrend(history, start);
    }

    @Override
    public void init(History history) {
        histories.put(history, new HistoryImpl());
    }

    @Override
    public HistoryDatabase makeDatabaseNode(DSMap parameters) {
        return new InMemoryDatabase();
    }

    @Override
    public DSDateTime purge(History history, int count) {
        HistoryImpl impl = histories.get(history);
        if (impl == null) {
            return DSDateTime.NULL;
        }
        HistoryRecord rec;
        while (count > 0) {
            rec = impl.removeFirst();
            if (rec == null) {
                return getFirstTimestamp(history);
            }
            count--;
        }
        return getFirstTimestamp(history);
    }

    @Override
    public DSDateTime purge(History history, DSTimeRange range) {
        HistoryImpl impl = histories.get(history);
        if (impl == null) {
            return DSDateTime.NULL;
        }
        DSDateTime end = range.getEnd();
        boolean hasEnd = !end.isNull();
        HistoryRecord prev = null;
        HistoryRecord rec = impl.head;
        while (rec != null) {
            if (hasEnd && rec.timestamp.isAfter(end)) {
                break;
            }
            if (range.contains(rec.timestamp)) {
                impl.remove(prev, rec);
            } else {
                prev = rec;
            }
            rec = rec.next;
        }
        return getFirstTimestamp(history);
    }

    @Override
    public void write(History history,
                      DSDateTime timestamp,
                      DSElement value,
                      DSStatus status) {
        HistoryRecord rec = new HistoryRecord();
        rec.timestamp = timestamp;
        rec.value = value;
        rec.status = status;
        HistoryImpl impl = histories.get(history);
        if (impl == null) {
            impl = new HistoryImpl();
            histories.put(history, impl);
        }
        impl.add(rec);
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

    private static class HistoryImpl {

        HistoryRecord head;
        int size;
        HistoryRecord tail;

        void add(HistoryRecord rec) {
            if (head == null) {
                head = tail = rec;
            } else {
                tail.next = rec;
                tail = rec;
            }
            size++;
        }

        void remove(HistoryRecord prev, HistoryRecord toRemove) {
            if (prev == null) {
                removeFirst();
            } else {
                prev.next = toRemove.next;
                size--;
            }
        }

        HistoryRecord removeFirst() {
            HistoryRecord ret = head;
            if (head != null) {
                head = ret.next;
                size--;
            }
            return ret;
        }
    }

    private static class HistoryRecord {

        HistoryRecord next;
        DSStatus status;
        DSDateTime timestamp;
        DSElement value;
    }

    private class InMemoryTrend implements DSITrend {

        private HistoryRecord current;
        private History history;
        private HistoryRecord next;

        public InMemoryTrend(History history, DSDateTime start) {
            this.history = history;
            HistoryImpl impl = histories.get(history);
            if (impl == null) {
                return;
            }
            next = impl.head;
            if (start.isNull()) {
                return;
            }
            if (next == null) {
                return;
            }
            while (next.timestamp.isBefore(start)) {
                next = next.next;
                if (next == null) {
                    break;
                }
            }
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public void getColumnMetadata(int index, DSMap bucket) {
            DSMetadata meta = new DSMetadata(bucket);
            switch (index) {
                case 0:
                    meta.setName("Timestamp");
                    DSMetadata.getMetadata(history.getInfo(History.TIMEZONE), bucket);
                    break;
                case 1:
                    meta.setName("Value");
                    DSMetadata.getMetadata(history.getInfo(History.WATCH_VALUE), bucket);
                    break;
                default: //case 2:
                    meta.setName("Status");
                    meta.setType(DSStatus.ok);
                    break;
            }
        }

        @Override
        public DSIValue getValue(int index) {
            switch (index) {
                case 0:
                    return current.timestamp;
                case 1:
                    return current.value;
                case 2:
                    return current.status;
            }
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }

        @Override
        public int getStatus() {
            return current.status.getBits();
        }

        @Override
        public int getStatusColumn() {
            return 2;
        }

        @Override
        public long getTimestamp() {
            return current.timestamp.timeInMillis();
        }

        @Override
        public int getTimestampColumn() {
            return 0;
        }

        @Override
        public DSElement getValue() {
            return current.value;
        }

        @Override
        public int getValueColumn() {
            return 1;
        }

        @Override
        public boolean next() {
            if (next == null) {
                return false;
            }
            current = next;
            next = next.next;
            return true;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

}
