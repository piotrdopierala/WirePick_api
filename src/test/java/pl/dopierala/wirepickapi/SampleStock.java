package pl.dopierala.wirepickapi;

import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

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

    private SampleStock() {
    }

    static {
        refreshValues();
    }

    public static void refreshValues() {
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
