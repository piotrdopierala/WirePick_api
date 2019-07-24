package pl.dopierala.wirepickapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.DateParseException;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.service.StockService;
import pl.dopierala.wirepickapi.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private StockService stockService;
    private UserService userService;

    @Autowired
    public StockController(StockService stockService, UserService userService) {
        this.stockService = stockService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public Iterable<DeviceItem> getAllDevices() {
        return stockService.findAllStock();
    }

    @GetMapping("/{stockItemId}")
    public DeviceItem getByItemId(@PathVariable Long stockItemId) {
        return stockService.findStockByItemId(stockItemId);
    }

    @GetMapping("/device/{deviceDefinitionId}")
    public Iterable<DeviceItem> getAllDevicesByDefinitionId(@PathVariable Long deviceDefinitionId) {
        return stockService.findStockByDeviceDefinition(deviceDefinitionId);
    }

    @GetMapping("/device/{deviceDefinitionId}/free/from/{deviceReservFrom}/to/{deviceReservTo}")
    public Iterable<DeviceItem> getAllAvailableItems(@PathVariable Long deviceDefinitionId,
                                                     @PathVariable String deviceReservFrom,
                                                     @PathVariable String deviceReservTo) throws DateParseException {

        LocalDateTime reserveDateFrom = parseDate(deviceReservFrom);
        LocalDateTime reserveDateTo = parseDate(deviceReservTo);

        return stockService.findFreeStockByDeviceDefinition(deviceDefinitionId, reserveDateFrom, reserveDateTo);
    }

    @PutMapping("/reserv/{stockItemId}/user/{userId}/from/{deviceReservFrom}/to/{deviceReservTo}")
    public ResponseEntity putHireDevice(@PathVariable Long stockItemId,
                                        @PathVariable Long userId,
                                        @PathVariable String deviceReservFrom,
                                        @PathVariable String deviceReservTo) throws DateParseException {

        LocalDateTime reservationDateFrom = parseDate(deviceReservFrom);
        LocalDateTime reservationDateTo = parseDate(deviceReservTo);

        User userById = userService.findUserById(userId);

        stockService.bookItem(stockItemId, reservationDateFrom, reservationDateTo, userById);

        return ResponseEntity.accepted().body("Device Reserved");
    }

    private LocalDateTime parseDate(String dateToParse) throws DateParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDate.parse(dateToParse, formatter).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new DateParseException("" + e.getParsedString());
        }
        return parsedDate;
    }
}
