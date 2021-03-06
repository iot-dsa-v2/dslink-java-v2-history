package org.iot.dsa.dslink.history;

import org.iot.dsa.dslink.history.value.HistoryAge;
import org.iot.dsa.time.Time;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;

public class HistoryAgeTest {

    @Test
    public void testDays() {
        HistoryAge ivl = HistoryAge.valueOf("5 Days");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.addDays(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testHours() {
        HistoryAge ivl = HistoryAge.valueOf("5 Hours");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.addHours(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testMinutes() {
        HistoryAge ivl = HistoryAge.valueOf("5 Minutes");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.addMinutes(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }

    @Test
    public void testMonths() {
        HistoryAge ivl = HistoryAge.valueOf("5 Months");
        HistoryAge tmp = HistoryAge.valueOf(ivl.toString());
        Assert.assertEquals(ivl, tmp);
        Assert.assertEquals(ivl.toString(), tmp.toString());
        long now = System.currentTimeMillis();
        Calendar ivlCal = Time.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.addMonths(-5, calTest);
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
        Calendar ivlCal = Time.getCalendar(now);
        ivl.apply(ivlCal);
        Calendar calTest = Time.getCalendar(now);
        calTest.setTimeInMillis(now);
        Time.addWeeks(-5, calTest);
        Assert.assertEquals(ivlCal, calTest);
    }


}
