package pl.dopierala.wirepickapi.repositories.devices;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.dopierala.wirepickapi.SampleStock;
import pl.dopierala.wirepickapi.repositories.user.UserRepository;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class BookingsRepositoryTest {

    @Autowired
    private BookingsRepository bookingsRepository;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DevicesDefinitionRepository devicesDefinitionRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void initialize_data() {
        //prepare users
        userRepository.save(SampleStock.u1);
        userRepository.save(SampleStock.u2);
        //prepare device definitions
        devicesDefinitionRepository.save(SampleStock.d1);
        devicesDefinitionRepository.save(SampleStock.d2);
        //prepare stock items
        SampleStock.s1_d1 = stockRepository.save(SampleStock.s1_d1);
        SampleStock.s2_d1 = stockRepository.save(SampleStock.s2_d1);
        SampleStock.s3_d1 = stockRepository.save(SampleStock.s3_d1);
        SampleStock.s4_d2 = stockRepository.save(SampleStock.s4_d2);
        SampleStock.s5_d2 = stockRepository.save(SampleStock.s5_d2);
    }

    @Test
    public void Should_numberOfOverlappingBookPeriods_return_zero() {
        int noOverlapBookPeriods = bookingsRepository.numberOfOverlappingBookPeriods(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 05, 11, 9, 0),
                LocalDateTime.of(2017, 05, 20, 9, 0)
        );

        Assert.assertEquals(0, noOverlapBookPeriods);
    }

    @Test
    public void Should_numberOfOverlappingBookPeriods_return_two() {
        int noOverlapBookPeriods = bookingsRepository.numberOfOverlappingBookPeriods(
                SampleStock.s1_d1.getId(),
                LocalDateTime.of(2017, 4, 11, 9, 0),
                LocalDateTime.of(2017, 6, 7, 9, 0)
        );

        Assert.assertEquals(2, noOverlapBookPeriods);
    }

    @Test
    public void Should_numberOfOverlappingRentPeriods_return_zero() {
        int noOverlapRentPeriods = bookingsRepository.numberOfOverlappingRentPeriods(
                SampleStock.s1_d1.getBookings().get(0).getId(),
                LocalDateTime.of(2017, 5, 7, 9, 0),
                LocalDateTime.of(2017, 5, 8, 9, 0)
        );

        Assert.assertEquals(0, noOverlapRentPeriods);
    }

    @Test
    public void Should_numberOfOverlappingRentPeriods_return_one() {
        int noOverlapRentPeriods = bookingsRepository.numberOfOverlappingRentPeriods(
                SampleStock.s1_d1.getBookings().get(0).getId(),
                LocalDateTime.of(2017, 5, 4, 9, 0),
                LocalDateTime.of(2017, 5, 8, 9, 0)
        );

        Assert.assertEquals(1, noOverlapRentPeriods);
    }

    @Test
    public void Should_numberOfOverlappingRentPeriods_return_two() {
        int noOverlapRentPeriods = bookingsRepository.numberOfOverlappingRentPeriods(
                SampleStock.s1_d1.getBookings().get(0).getId(),
                LocalDateTime.of(2017, 5, 1, 13, 0),
                LocalDateTime.of(2017, 5, 8, 9, 0)
        );

        Assert.assertEquals(2, noOverlapRentPeriods);
    }

}