package com.example.recipeapp.utilities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class TimeUtilsTest extends TestCase {
    public final CurrentTimeProvider currentTimeProvider =  mock(CurrentTimeProvider.class);
    public final Date createdAt = new Date(991L);

    //  Test method convention: Given,When,Then

    @Test
    public void givenJustPosted_WhenCalculateTimeAgo_ThenReturnJustNow() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(2000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "just now");
    }

    @Test
    public void givenPostedAMinuteAgo_WhenCalculateTimeAgo_ThenReturnAMinuteAgo() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(65000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "a minute ago");
    }

    @Test
    public void givenPostedMinutesAgo_WhenCalculateTimeAgo_ThenReturnMinutesAgo() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(240000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "3 m");
    }

    @Test
    public void givenPostedAnHourAgo_WhenCalculateTimeAgo_ThenReturnAnHourAgo() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(5000000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "an hour ago");
    }

    @Test
    public void givenPostedHoursAgo_WhenCalculateTimeAgo_ThenReturnHoursAgo() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(70000000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "19 h");
    }

    @Test
    public void givenPostedYesterday_WhenCalculateTimeAgo_ThenReturnYesterday() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(170000000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "yesterday");
    }

    @Test
    public void givenPostedDaysAgo_WhenCalculateTimeAgo_ThenReturnDaysAgo() {
        when(currentTimeProvider.getCurrentTime()).thenReturn(180000000L);
        TimeUtils timeUtils = new TimeUtils(currentTimeProvider);
        assertEquals(timeUtils.calculateTimeAgo(createdAt), "2 d");
    }
}