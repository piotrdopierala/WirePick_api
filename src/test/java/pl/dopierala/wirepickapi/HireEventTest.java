package pl.dopierala.wirepickapi;

import org.junit.Assert;
import org.junit.Test;
import pl.dopierala.wirepickapi.model.HireEvent;

import java.time.LocalDateTime;

public class HireEventTest {

    private HireEvent testHireEvent;
    private LocalDateTime testDate = LocalDateTime.now();

    @Test
    public void Should_isInHirePeriod_ReturnTrue_When_DeviceNowHired() {
        testHireEvent = new HireEvent(testDate.minusHours(10), testDate.plusHours(10),SampleUsers.u1);
        Assert.assertTrue(testHireEvent.isInHirePeriod());
    }

    @Test
    public void Should_isInHirePeriod_ReturnFalse_When_HirePeriod_NOT_include_Now() {
        HireEvent testHireEventBefore = new HireEvent(testDate.minusHours(10), testDate.minusHours(5),SampleUsers.u1);
        HireEvent testHireEventAfter = new HireEvent(testDate.plusHours(10), testDate.plusHours(15),SampleUsers.u1);

        Assert.assertFalse(testHireEventBefore.isInHirePeriod());
        Assert.assertFalse(testHireEventAfter.isInHirePeriod());
    }

    @Test
    public void Should_isInHirePeriod_ReturnTrue_When_HirePeriod_including_TestPeriodStart() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.plusDays(2);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertTrue(testHireEvent.isInHirePeriod(testStart));
        Assert.assertFalse(testHireEvent.isInHirePeriod(testEnd));
        Assert.assertTrue(testHireEvent.isInHirePeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInHirePeriod_ReturnTrue_When_HirePeriod_including_TestPeriodStop() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(2);

        Assert.assertTrue(testHireEvent.isInHirePeriod(testEnd));
        Assert.assertFalse(testHireEvent.isInHirePeriod(testStart));
        Assert.assertTrue(testHireEvent.isInHirePeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInHirePeriod_ReturnTrue_When_WholeTestPeriod_Includes_HirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertFalse(testHireEvent.isInHirePeriod(testEnd));
        Assert.assertFalse(testHireEvent.isInHirePeriod(testStart));
        Assert.assertTrue(testHireEvent.isInHirePeriod(testStart, testEnd));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_Start() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertTrue(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_End() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertTrue(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_WholeTestPeriodInsideHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(2), testDate.plusDays(2),SampleUsers.u1);

        Assert.assertTrue(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodBeforeHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodAfterHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.plusDays(20), testDate.plusDays(25),SampleUsers.u1);

        Assert.assertFalse(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_TestPeriodSameAsHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testHireEvent.getHireStart(), testHireEvent.getHireEnd(),SampleUsers.u1);

        Assert.assertTrue(testHireEvent.isOverlapping(otherHireEvent));
    }

    @Test
    public void Should_isContaining_ReturnTrue_When_WholeTestPeriodInsideHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(5), testDate.plusDays(5),SampleUsers.u1);

        Assert.assertTrue(testHireEvent.isContaining(otherHireEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_WholeTestPeriodBeforeHirePeriod() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testHireEvent.isContaining(otherHireEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingStartOnly() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertFalse(testHireEvent.isContaining(otherHireEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingEndOnly() {
        testHireEvent = new HireEvent(testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertFalse(testHireEvent.isContaining(otherHireEvent));
    }

    @Test
    public void Should_compareTo_return_0_When_StartDatesAreTheSame(){
        testHireEvent = new HireEvent(testDate.minusDays(10),testDate.plusDays(10),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(10),testDate.plusDays(20),SampleUsers.u1);

        Assert.assertEquals(testHireEvent.compareTo(otherHireEvent),0);
    }

    @Test
    public void Should_compareTo_return_minus2_When_TestHireEvent_Before_Other_HireEvent(){
        testHireEvent = new HireEvent(testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testHireEvent.compareTo(otherHireEvent),-2);
    }

    @Test
    public void Should_compareTo_return_minus1_When_TestHireEventStart_before_other_HireEventStart_And_Overlapping(){
        testHireEvent = new HireEvent(testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(25),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testHireEvent.compareTo(otherHireEvent),-1);
    }

    @Test
    public void Should_compareTo_return_2_When_TestHireEvent_After_Other_HireEvent(){
        testHireEvent = new HireEvent(testDate.plusDays(30),testDate.plusDays(40),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testHireEvent.compareTo(otherHireEvent),2);
    }

    @Test
    public void Should_compareTo_return_1_When_TestHireEventStart_after_other_HireEventStart_And_Overlapping(){
        testHireEvent = new HireEvent(testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        HireEvent otherHireEvent = new HireEvent(testDate.minusDays(35),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testHireEvent.compareTo(otherHireEvent),1);
    }

}