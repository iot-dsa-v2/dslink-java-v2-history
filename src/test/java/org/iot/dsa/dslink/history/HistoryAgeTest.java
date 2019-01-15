package org.iot.dsa.dslink.history;

import java.util.Calendar;
import org.iot.dsa.time.DSTime;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HistoryAgeTest {

    @Test
    public void testDays() {
        HistoryAge ivl = HistoryAge.valueOf("5 Days");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = DSTime.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = DSTime.getCalendar(now);
        calTest.setTimeInMillis(now);
        DSTime.addDays(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testHours() {
        HistoryAge ivl = HistoryAge.valueOf("5 Hours");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = DSTime.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = DSTime.getCalendar(now);
        calTest.setTimeInMillis(now);
        DSTime.addHours(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testMinutes() {
        HistoryAge ivl = HistoryAge.valueOf("5 Minutes");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = DSTime.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = DSTime.getCalendar(now);
        calTest.setTimeInMillis(now);
        DSTime.addMinutes(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testMonths() {
        HistoryAge ivl = HistoryAge.valueOf("5 Months");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = DSTime.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = DSTime.getCalendar(now);
        calTest.setTimeInMillis(now);
        DSTime.addMonths(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testOff() {
        HistoryAge ivl = HistoryAge.valueOf("Off");
        Assert.assertEquals(ivl, HistoryAge.NULL);
    }

    @Test
    public void testWeeks() {
        HistoryAge ivl = HistoryAge.valueOf("5 Weeks");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = DSTime.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = DSTime.getCalendar(now);
        calTest.setTimeInMillis(now);
        DSTime.addWeeks(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }


}
