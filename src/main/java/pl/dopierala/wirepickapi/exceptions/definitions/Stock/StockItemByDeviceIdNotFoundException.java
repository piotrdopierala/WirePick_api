package pl.dopierala.wirepickapi.exceptions.definitions.Stock;

public class StockItemByDeviceIdNotFoundException extends RuntimeException {
    public StockItemByDeviceIdNotFoundException(String s) {
        super(s);
    }
}
