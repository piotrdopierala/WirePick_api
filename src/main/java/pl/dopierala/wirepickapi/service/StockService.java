package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.UserNotFoundException;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class StockService {

    private StockRepository stockRepository;
    private UserService userService;

    @Autowired
    public StockService(StockRepository stockRepository, UserService userService) {
        this.stockRepository = stockRepository;
        this.userService = userService;
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

    public Iterable<DeviceItem> findStockByDeviceDefinition(Long id) {
        Iterable<DeviceItem> itemsFoundByDeviceDefinitionId = stockRepository.findByDeviceDefinition_Id(id);
        if (itemsFoundByDeviceDefinitionId.iterator().hasNext()) {
            return itemsFoundByDeviceDefinitionId;
        } else {
            throw new StockItemByDeviceIdNotFoundException("Stock item by device id '" + id + "' not found even single one.");
        }
    }

    /**
     *
     * @param itemId Stock item ID that should be hired
     * @param from Date from witch Stock item should be hired
     * @param to Date to witch Stock item should be hired
     * @param userId User ID that rents Stock item
     * @return 0 if rent is confirmed
     * @throws UserNotFoundException when user ID is not found.
     * @throws StockItemIdNotFoundException when stock item ID is not found.
     */
    public int hireItem(Long itemId, LocalDateTime from, LocalDateTime to, Long userId) throws UserNotFoundException, StockItemIdNotFoundException {
        User userRenting = userService.findUserById(userId);
        DeviceItem stockItemToRent = findStockByItemId(itemId);


        stockItemToRent.rent(from, to, userRenting);
        stockRepository.save(stockItemToRent);
        return 0;
    }
}
