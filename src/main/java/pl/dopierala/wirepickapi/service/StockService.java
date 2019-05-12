package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.exceptions.definitions.DeviceNotAvailableAlreadyHiredException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.HireEvent;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.devices.HireRepository;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class StockService {

    private StockRepository stockRepository;
    private HireRepository hireRepository;

    @Autowired
    public StockService(StockRepository stockRepository, HireRepository hireRepository) {

        this.stockRepository = stockRepository;
        this.hireRepository = hireRepository;
    }

    public Iterable<DeviceItem> findAllStock() {
        return stockRepository.findAll();
    }

    public DeviceItem findStockByItemId(Long id) throws StockItemIdNotFoundException {
        Optional<DeviceItem> itemFoundById = stockRepository.findById(id);
        if (itemFoundById.isPresent()) {
            return itemFoundById.get();
        } else {
            throw new StockItemIdNotFoundException("Stock item id '" + id + "' not found.");
        }
    }

    public Iterable<DeviceItem> findStockByDeviceDefinition(Long deviceDefinitionId) throws StockItemByDeviceIdNotFoundException {
        Iterable<DeviceItem> itemsFoundByDeviceDefinitionId = stockRepository.findByDeviceDefinition_Id(deviceDefinitionId);
        if (itemsFoundByDeviceDefinitionId.iterator().hasNext()) {
            return itemsFoundByDeviceDefinitionId;
        } else {
            throw new StockItemByDeviceIdNotFoundException("Stock item by device definition id '" + deviceDefinitionId + "' not found even single one.");
        }
    }

    public Iterable<DeviceItem> findFreeStockByDeviceDefinition(Long deviceDefinitionId, LocalDateTime from, LocalDateTime to) {
        return stockRepository.findFreeItemsByDeviceIdAndHirePeriod(deviceDefinitionId,from,to);
    }

    /**
     * Rents device for given user from supplied date for given Duration
     *
     * @param itemId   device to rent
     * @param start    DateTime start of rent period
     * @param duration Duration of rent period
     * @param user     User witch rents device
     * @return 0 - rent succeeded
     */
    public int rentItem(Long itemId, LocalDateTime start, Duration duration, User user) throws StockItemIdNotFoundException, DeviceNotAvailableAlreadyHiredException {
        DeviceItem itemFoundById = findStockByItemId(itemId);
        if (isAvailable(itemId, start, duration)) {
            itemFoundById.getHires().add(new HireEvent(itemFoundById, start, duration, user));
            stockRepository.save(itemFoundById);
            return 0;
        } else {
            throw new DeviceNotAvailableAlreadyHiredException();
        }
    }

    /**
     * Rents device for given user between supplied dates
     *
     * @param itemId device to rent
     * @param start  DateTime start of rent period
     * @param end    DateTime end of rent period
     * @param user   User witch rents device
     * @return 0 - rent succeeded
     */
    public int rentItem(Long itemId, LocalDateTime start, LocalDateTime end, User user) throws StockItemIdNotFoundException, UserNotFoundException, DeviceNotAvailableAlreadyHiredException {
        return rentItem(itemId, start, Duration.between(start, end), user);
    }

    /**
     * Checks whether device item is available to borrow given start date and period od hire.
     *
     * @param itemId  Stock item ID that will be checked if available
     * @param when    Start date of hire period
     * @param howLong Period of hire
     * @return if deviceDefinition is available or not
     */
    public boolean isAvailable(Long itemId, LocalDateTime when, Duration howLong) {

        if (Objects.isNull(itemId) || Objects.isNull(when) || Objects.isNull(howLong)) {
            return false;
        }
        return isAvailable(itemId, when, when.plus(howLong));
    }

    /**
     * Checks whether device item is available to borrow given start date and period od hire.
     *
     * @param itemId Stock item ID that will be checked if available
     * @param from   Start date of hire period
     * @param end    End date of hire period
     * @return if deviceDefinition is available or not
     */
    public boolean isAvailable(Long itemId, LocalDateTime from, LocalDateTime end) {
        if (Objects.isNull(itemId) || Objects.isNull(from) || Objects.isNull(end)) {
            return false;
        }
        return (hireRepository.numberOfOverlappingHirePeriods(itemId, from, end) == 0);
    }
}
