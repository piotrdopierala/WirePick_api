package pl.dopierala.wirepickapi;

import pl.dopierala.wirepickapi.model.device.DeviceItem;

import java.util.List;

public class Utils {
    public static <T> int getIterableSize(Iterable<T> iterable) {
        int allItemsSize = 0;
        if (iterable instanceof List) {
            allItemsSize = ((List) iterable).size();
        } else {
            for (T item : iterable) {
                allItemsSize++;
            }
        }
        return allItemsSize;
    }
}
