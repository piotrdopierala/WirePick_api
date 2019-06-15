package pl.dopierala.wirepickapi;

import org.junit.Assert;
import org.junit.Test;
import pl.dopierala.wirepickapi.model.ReservationEvent;

import java.time.LocalDateTime;

public class ReservationEventTest {

    private ReservationEvent testReservationEvent;
    private LocalDateTime testDate = LocalDateTime.now();

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_DeviceNowReserved() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusHours(10), testDate.plusHours(10),SampleUsers.u1);
        Assert.assertTrue(testReservationEvent.isInReservPeriod());
    }

    @Test
    public void Should_isInReservPeriod_ReturnFalse_When_ReservationPeriod_NOT_include_Now() {
        ReservationEvent testReservationEventBefore = new ReservationEvent(SampleStock.s1_d1,testDate.minusHours(10), testDate.minusHours(5),SampleUsers.u1);
        ReservationEvent testReservationEventAfter = new ReservationEvent(SampleStock.s1_d1,testDate.plusHours(10), testDate.plusHours(15),SampleUsers.u1);

        Assert.assertFalse(testReservationEventBefore.isInReservPeriod());
        Assert.assertFalse(testReservationEventAfter.isInReservPeriod());
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_ReservationPeriod_including_TestPeriodStart() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.plusDays(2);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertTrue(testReservationEvent.isInReservPeriod(testStart));
        Assert.assertFalse(testReservationEvent.isInReservPeriod(testEnd));
        Assert.assertTrue(testReservationEvent.isInReservPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_ReservationPeriod_including_TestPeriodStop() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(2);

        Assert.assertTrue(testReservationEvent.isInReservPeriod(testEnd));
        Assert.assertFalse(testReservationEvent.isInReservPeriod(testStart));
        Assert.assertTrue(testReservationEvent.isInReservPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_WholeTestPeriod_Includes_ReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertFalse(testReservationEvent.isInReservPeriod(testEnd));
        Assert.assertFalse(testReservationEvent.isInReservPeriod(testStart));
        Assert.assertTrue(testReservationEvent.isInReservPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_Start() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertTrue(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_End() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertTrue(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_WholeTestPeriodInsideReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(2), testDate.plusDays(2),SampleUsers.u1);

        Assert.assertTrue(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodBeforeReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodAfterReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.plusDays(20), testDate.plusDays(25),SampleUsers.u1);

        Assert.assertFalse(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_TestPeriodSameAsReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1, testReservationEvent.getReservStart(), testReservationEvent.getReservEnd(),SampleUsers.u1);

        Assert.assertTrue(testReservationEvent.isOverlapping(otherReservationEvent));
    }

    @Test
    public void Should_isContaining_ReturnTrue_When_WholeTestPeriodInsideReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(5), testDate.plusDays(5),SampleUsers.u1);

        Assert.assertTrue(testReservationEvent.isContaining(otherReservationEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_WholeTestPeriodBeforeReservationPeriod() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testReservationEvent.isContaining(otherReservationEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingStartOnly() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertFalse(testReservationEvent.isContaining(otherReservationEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingEndOnly() {
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertFalse(testReservationEvent.isContaining(otherReservationEvent));
    }

    @Test
    public void Should_compareTo_return_0_When_StartDatesAreTheSame(){
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.plusDays(10),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.plusDays(20),SampleUsers.u1);

        Assert.assertEquals(testReservationEvent.compareTo(otherReservationEvent),0);
    }

    @Test
    public void Should_compareTo_return_minus2_When_TestReservationEvent_Before_Other_ReservationEvent(){
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testReservationEvent.compareTo(otherReservationEvent),-2);
    }

    @Test
    public void Should_compareTo_return_minus1_When_TestReservationEventStart_before_other_ReservationEventStart_And_Overlapping(){
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(25),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testReservationEvent.compareTo(otherReservationEvent),-1);
    }

    @Test
    public void Should_compareTo_return_2_When_TestReservationEvent_After_Other_ReservationEvent(){
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.plusDays(30),testDate.plusDays(40),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testReservationEvent.compareTo(otherReservationEvent),2);
    }

    @Test
    public void Should_compareTo_return_1_When_TestReservationEventStart_after_other_ReservationEventStart_And_Overlapping(){
        testReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        ReservationEvent otherReservationEvent = new ReservationEvent(SampleStock.s1_d1,testDate.minusDays(35),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testReservationEvent.compareTo(otherReservationEvent),1);
    }

}