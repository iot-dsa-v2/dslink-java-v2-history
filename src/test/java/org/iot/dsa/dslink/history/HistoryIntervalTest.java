package org.iot.dsa.dslink.history;

import java.util.Calendar;
import org.iot.dsa.time.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HistoryIntervalTest {

    @Test
    public void testHours() {
        HistoryInterval ivl = HistoryInterval.valueOf("5 Hours");
        HistoryInterval tmp = HistoryInterval.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.align(ivlCal);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.alignHours(5, calTest);
        Time.addHours(5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testMinutes() {
        HistoryInterval ivl = HistoryInterval.valueOf("5 Minutes");
        HistoryInterval tmp = HistoryInterval.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.align(ivlCal);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.alignMinutes(5, calTest);
        Time.addMinutes(5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testOff() {
        HistoryInterval ivl = HistoryInterval.valueOf("Off");
        Assert.assertEquals(ivl, HistoryInterval.NULL);
    }

    @Test
    public void testSeconds() {
        HistoryInterval ivl = HistoryInterval.valueOf("5 Seconds");
        HistoryInterval tmp = HistoryInterval.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.align(ivlCal);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.alignSeconds(5, calTest);
        Time.addSeconds(5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

}
