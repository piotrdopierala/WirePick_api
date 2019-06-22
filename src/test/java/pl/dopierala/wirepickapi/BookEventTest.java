package pl.dopierala.wirepickapi;

import org.junit.Assert;
import org.junit.Test;
import pl.dopierala.wirepickapi.model.BookEvent;

import java.time.LocalDateTime;

public class BookEventTest {

    private BookEvent testBookEvent;
    private LocalDateTime testDate = LocalDateTime.now();

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_DeviceNowReserved() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusHours(10), testDate.plusHours(10),SampleUsers.u1);
        Assert.assertTrue(testBookEvent.isInBookedPeriod());
    }

    @Test
    public void Should_isInReservPeriod_ReturnFalse_When_ReservationPeriod_NOT_include_Now() {
        BookEvent testBookEventBefore = new BookEvent(SampleStock.s1_d1,testDate.minusHours(10), testDate.minusHours(5),SampleUsers.u1);
        BookEvent testBookEventAfter = new BookEvent(SampleStock.s1_d1,testDate.plusHours(10), testDate.plusHours(15),SampleUsers.u1);

        Assert.assertFalse(testBookEventBefore.isInBookedPeriod());
        Assert.assertFalse(testBookEventAfter.isInBookedPeriod());
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_ReservationPeriod_including_TestPeriodStart() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.plusDays(2);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertTrue(testBookEvent.isInBookedPeriod(testStart));
        Assert.assertFalse(testBookEvent.isInBookedPeriod(testEnd));
        Assert.assertTrue(testBookEvent.isInBookedPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_ReservationPeriod_including_TestPeriodStop() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(5),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(2);

        Assert.assertTrue(testBookEvent.isInBookedPeriod(testEnd));
        Assert.assertFalse(testBookEvent.isInBookedPeriod(testStart));
        Assert.assertTrue(testBookEvent.isInBookedPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isInReservPeriod_ReturnTrue_When_WholeTestPeriod_Includes_ReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        LocalDateTime testStart = testDate.minusDays(20);
        LocalDateTime testEnd = testDate.plusDays(20);

        Assert.assertFalse(testBookEvent.isInBookedPeriod(testEnd));
        Assert.assertFalse(testBookEvent.isInBookedPeriod(testStart));
        Assert.assertTrue(testBookEvent.isInBookedPeriod(testStart, testEnd));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_Start() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertTrue(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_Overlapping_End() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertTrue(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_WholeTestPeriodInsideReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(2), testDate.plusDays(2),SampleUsers.u1);

        Assert.assertTrue(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodBeforeReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnFalse_When_WholeTestPeriodAfterReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.plusDays(20), testDate.plusDays(25),SampleUsers.u1);

        Assert.assertFalse(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isOverlapping_ReturnTrue_When_TestPeriodSameAsReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1, testBookEvent.getBookStart(), testBookEvent.getBookEnd(),SampleUsers.u1);

        Assert.assertTrue(testBookEvent.isOverlapping(otherBookEvent));
    }

    @Test
    public void Should_isContaining_ReturnTrue_When_WholeTestPeriodInsideReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(5), testDate.plusDays(5),SampleUsers.u1);

        Assert.assertTrue(testBookEvent.isContaining(otherBookEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_WholeTestPeriodBeforeReservationPeriod() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate.minusDays(15),SampleUsers.u1);

        Assert.assertFalse(testBookEvent.isContaining(otherBookEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingStartOnly() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate, testDate.plusDays(20),SampleUsers.u1);

        Assert.assertFalse(testBookEvent.isContaining(otherBookEvent));
    }

    @Test
    public void Should_isContaining_ReturnFalse_When_OverlappingEndOnly() {
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10), testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(20), testDate,SampleUsers.u1);

        Assert.assertFalse(testBookEvent.isContaining(otherBookEvent));
    }

    @Test
    public void Should_compareTo_return_0_When_StartDatesAreTheSame(){
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.plusDays(10),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.plusDays(20),SampleUsers.u1);

        Assert.assertEquals(testBookEvent.compareTo(otherBookEvent),0);
    }

    @Test
    public void Should_compareTo_return_minus2_When_TestReservationEvent_Before_Other_ReservationEvent(){
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testBookEvent.compareTo(otherBookEvent),-2);
    }

    @Test
    public void Should_compareTo_return_minus1_When_TestReservationEventStart_before_other_ReservationEventStart_And_Overlapping(){
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(25),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testBookEvent.compareTo(otherBookEvent),-1);
    }

    @Test
    public void Should_compareTo_return_2_When_TestReservationEvent_After_Other_ReservationEvent(){
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.plusDays(30),testDate.plusDays(40),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(10),testDate.minusDays(2),SampleUsers.u1);

        Assert.assertEquals(testBookEvent.compareTo(otherBookEvent),2);
    }

    @Test
    public void Should_compareTo_return_1_When_TestReservationEventStart_after_other_ReservationEventStart_And_Overlapping(){
        testBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(30),testDate.minusDays(20),SampleUsers.u1);
        BookEvent otherBookEvent = new BookEvent(SampleStock.s1_d1,testDate.minusDays(35),testDate.minusDays(10),SampleUsers.u1);

        Assert.assertEquals(testBookEvent.compareTo(otherBookEvent),1);
    }

}