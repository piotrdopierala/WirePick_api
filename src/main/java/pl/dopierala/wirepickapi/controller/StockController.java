package pl.dopierala.wirepickapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.HireDateParseException;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.model.user.User;
import pl.dopierala.wirepickapi.service.DeviceService;
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

    @GetMapping("/{deviceItemId}")
    public DeviceItem getByItemId(@PathVariable Long deviceItemId) {
        return stockService.findStockByItemId(deviceItemId);
    }

    @GetMapping("/device/{deviceDefinitionId}")
    public Iterable<DeviceItem> getAllDevicesByDefinitionId(@PathVariable Long deviceDefinitionId) {
        return stockService.findStockByDeviceDefinition(deviceDefinitionId);
    }

    @GetMapping("/device/{deviceDefinitionId}/free/from/{deviceHireFrom}/to/{deviceHireTo}")
    public Iterable<DeviceItem> getAllAvailableItems(@PathVariable Long deviceDefinitionId,
                                                     @PathVariable String deviceHireFrom,
                                                     @PathVariable String deviceHireTo) throws HireDateParseException {

        LocalDateTime hireDateFrom = parseDate(deviceHireFrom);
        LocalDateTime hireDateTo = parseDate(deviceHireTo);

        Iterable<DeviceItem> stockByDeviceDefinition = stockService.findStockByDeviceDefinition(deviceDefinitionId);

        return null;//todo finish
    }

    //TODO what happens when deviceItemId or userId can't be parsed to Long? Intercept this exception.
    @PutMapping("/hire/{deviceItemId}/user/{userId}/from/{deviceHireFrom}/to/{deviceHireTo}")
    public ResponseEntity putHireDevice(@PathVariable Long deviceItemId,
                                        @PathVariable Long userId,
                                        @PathVariable String deviceHireFrom,
                                        @PathVariable String deviceHireTo) throws HireDateParseException {

        LocalDateTime hireDateFrom = parseDate(deviceHireFrom);
        LocalDateTime hireDateTo = parseDate(deviceHireTo);

        User userById = userService.findUserById(userId);

        stockService.rentItem(deviceItemId, hireDateFrom, hireDateTo, userById);

        return ResponseEntity.accepted().body("Device Hired");
    }

    private LocalDateTime parseDate(String dateToParse) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDate.parse(dateToParse, formatter).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new HireDateParseException("" + e.getParsedString());
        }
        return parsedDate;
    }
}
