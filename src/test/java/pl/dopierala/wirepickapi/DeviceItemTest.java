package pl.dopierala.wirepickapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyHiredException;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.HireEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeviceItemTest {

    private DeviceItem testItemCurrentlyHired;
    private DeviceItem testItemCurrentlyFree;
    private LocalDateTime now;

    @Before
    public void prepareTestItem() {

        List<HireEvent> sampleHiresFree = new ArrayList<>();
        now = LocalDateTime.now();

        sampleHiresFree.add(new HireEvent(now.minusDays(20), now.minusDays(10), SampleUsers.u1));
        sampleHiresFree.add(new HireEvent(now.minusDays(9), now.minusDays(8), SampleUsers.u1));
        sampleHiresFree.add(new HireEvent(now.plusDays(10), now.plusDays(20), SampleUsers.u2));
        testItemCurrentlyFree = new DeviceItem(sampleHiresFree);

        List<HireEvent> sampleHiresHired = new ArrayList<>(sampleHiresFree);
        sampleHiresHired.add(2, new HireEvent(now.minusDays(1), now.plusDays(1), SampleUsers.u1));
        testItemCurrentlyHired = new DeviceItem(sampleHiresHired);
    }

    @Test
    public void Should_isAvailable_ReturnTrue_When_DeviceNowFree() {
        Assert.assertTrue(testItemCurrentlyFree.isAvailable(LocalDateTime.now(), Duration.ofDays(1)));
    }

    @Test
    public void Should_isAvailable_ReturnFalse_When_DeviceNowHired() {
        Assert.assertFalse(testItemCurrentlyHired.isAvailable(LocalDateTime.now(), Duration.ofDays(1)));
    }

    @Test
    public void Should_rent_SuccessfullyHire_When_DeviceNowFree() {

        Assert.assertEquals(testItemCurrentlyFree.rent(LocalDateTime.now(), Duration.ofDays(1), SampleUsers.u1), 0);
        Assert.assertEquals(testItemCurrentlyFree.getHires().size(), 4);

    }

    @Test(expected = DeviceNotAvailableAlreadyHiredException.class)
    public void Should_rent_ThrowException_When_DeviceNowHired() {

        testItemCurrentlyHired.rent(LocalDateTime.now(), Duration.ofDays(2), SampleUsers.u1);

    }


}