package pl.dopierala.wirepickapi;

import pl.dopierala.wirepickapi.model.BookEvent;
import pl.dopierala.wirepickapi.model.BorrowEvent;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.Roles;
import pl.dopierala.wirepickapi.model.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleStock {
    public static DeviceDefinition d1;
    public static DeviceDefinition d2;
    public static Iterable<DeviceDefinition> sampleDevices;

    public static DeviceItem s1_d1;
    public static DeviceItem s2_d1;
    public static DeviceItem s3_d1;
    public static DeviceItem s4_d2;
    public static DeviceItem s5_d2;
    public static Iterable<DeviceItem> sampleStock;
    public static Iterable<DeviceItem> sampleStockOfDeviceDef2;

    public static User u1;
    public static User u2;

    private static List<BookEvent> s1_bookings;
    private static List<BorrowEvent> s1_u1_b1_borrows;
    private static List<BorrowEvent> s1_u1_b2_borrows;
    private static List<BorrowEvent> s1_u2_b1_borrows;

    public static BookEvent s1_u1_book1;
    public static BookEvent s1_u1_book2;
    public static BookEvent s1_u2_book1;

    public static BorrowEvent s1_u1_b1_borrow1;
    public static BorrowEvent s1_u1_b1_borrow2;
    public static BorrowEvent s1_u1_b2_borrow1;
    public static BorrowEvent s1_u2_b1_borrow1;


    private SampleStock() {
    }

    static {
        refreshValues();
    }

    public static void refreshValues() {
        generateUsers();
        generateStock();
        generateBorrowHistory();
    }

    private static void generateStock() {
        d1 = new DeviceDefinition();
        d1.setId(1);
        d1.setName("mock1");
        d2 = new DeviceDefinition();
        d2.setId(2);
        d2.setName("mock2");
        sampleDevices = new ArrayList<>(Arrays.asList(d1, d2));

        s1_d1 = new DeviceItem(d1);
        s1_d1.setId(1);
        s2_d1 = new DeviceItem(d1);
        s2_d1.setId(2);
        s3_d1 = new DeviceItem(d1);
        s3_d1.setId(3);

        s4_d2 = new DeviceItem(d2);
        s4_d2.setId(4);
        s5_d2 = new DeviceItem(d2);
        s5_d2.setId(5);

        sampleStock = new ArrayList<>(Arrays.asList(s1_d1, s2_d1, s3_d1, s4_d2, s5_d2));
        sampleStockOfDeviceDef2 = new ArrayList<>(Arrays.asList(s4_d2, s5_d2));
    }

    private static void generateUsers() {
        u1 = User.builder()
                .withFirstName("UserSample1")
                .withLogin("u1")
                .withPassword("u1")
                .withRole(Roles.USER)
                .build();
        u2 = User.builder()
                .withFirstName("UserSample2")
                .withLogin("u2")
                .withPassword("u2")
                .withRole(Roles.USER)
                .build();
    }

    private static void generateBorrowHistory() {
        s1_u1_book1 = new BookEvent(
                s1_d1,
                LocalDateTime.of(2017, 05, 1, 9, 0),
                LocalDateTime.of(2017, 05, 10, 9, 0),
                u1
        );
        s1_u1_book1.setId(1L);

        s1_u1_book2 = new BookEvent(
                s1_d1,
                LocalDateTime.of(2017, 07, 1, 9, 0),
                LocalDateTime.of(2017, 07, 10, 9, 0),
                u1
        );
        s1_u1_book2.setId(2L);

        s1_u2_book1 = new BookEvent(
                s1_d1,
                LocalDateTime.of(2017, 06, 1, 9, 0),
                LocalDateTime.of(2017, 06, 10, 9, 0),
                u2
        );
        s1_u2_book1.setId(3L);

        s1_u1_b1_borrow1 = new BorrowEvent(
                s1_u1_book1,
                LocalDateTime.of(2017, 05, 1, 9, 0),
                LocalDateTime.of(2017, 05, 2, 9, 0)
                );
        s1_u1_b1_borrow1.setId(1L);

        s1_u1_b1_borrow2 = new BorrowEvent(
                s1_u1_book1,
                LocalDateTime.of(2017, 05, 4, 9, 0),
                LocalDateTime.of(2017, 05, 6, 9, 0)
                );
        s1_u1_b1_borrow2.setId(2L);

        s1_u1_b2_borrow1 =  new BorrowEvent(
                s1_u1_book2,
                LocalDateTime.of(2017, 07, 1, 9, 0),
                LocalDateTime.of(2017, 07, 10, 9, 0)
        );
        s1_u1_b2_borrow1.setId(3L);

        s1_u2_b1_borrow1 = new BorrowEvent(
                s1_u2_book1,
                LocalDateTime.of(2017, 06, 2, 9, 0),
                LocalDateTime.of(2017, 06, 3, 9, 0)
        );
        s1_u2_b1_borrow1.setId(4L);

        s1_u1_b1_borrows = new ArrayList<>();
        s1_u1_b1_borrows.add(s1_u1_b1_borrow1);
        s1_u1_b1_borrows.add(s1_u1_b1_borrow2);

        s1_u1_b2_borrows = new ArrayList<>();
        s1_u1_b2_borrows.add(s1_u1_b2_borrow1);

        s1_u2_b1_borrows = new ArrayList<>();
        s1_u2_b1_borrows.add(s1_u2_b1_borrow1);

        s1_u1_book1.setBorrows(s1_u1_b1_borrows);
        s1_u1_book2.setBorrows(s1_u1_b2_borrows);
        s1_u2_book1.setBorrows(s1_u2_b1_borrows);

        s1_bookings = new ArrayList<>();
        s1_bookings.add(s1_u1_book1);
        s1_bookings.add(s1_u1_book2);
        s1_bookings.add(s1_u2_book1);

        s1_d1.setBookings(s1_bookings);
    }


    public static int getIterableSize(Iterable<DeviceItem> allItemsStock) {
        int allItemsSize = 0;
        if (allItemsStock instanceof List) {
            allItemsSize = ((List) allItemsStock).size();
        } else {
            for (DeviceItem deviceItem : allItemsStock) {
                allItemsSize++;
            }
        }
        return allItemsSize;
    }

    public static DeviceDefinition getD1() {
        return d1;
    }

    public static DeviceDefinition getD2() {
        return d2;
    }

    public static Iterable<DeviceDefinition> getSampleDevices() {
        return sampleDevices;
    }

    public static DeviceItem getS1_d1() {
        return s1_d1;
    }

    public static DeviceItem getS2_d1() {
        return s2_d1;
    }

    public static DeviceItem getS3_d1() {
        return s3_d1;
    }

    public static DeviceItem getS4_d2() {
        return s4_d2;
    }

    public static DeviceItem getS5_d2() {
        return s5_d2;
    }

    public static Iterable<DeviceItem> getSampleStock() {
        return sampleStock;
    }

    public static Iterable<DeviceItem> getSampleStockOfDeviceDef2() {
        return sampleStockOfDeviceDef2;
    }
}
