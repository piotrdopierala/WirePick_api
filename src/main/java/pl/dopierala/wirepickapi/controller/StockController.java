package pl.dopierala.wirepickapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.HireDateParseException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.service.StockService;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/all")
    public Iterable<DeviceItem> getAllDevices(){
        return stockService.findAllStock();
    }

    @GetMapping("/{deviceItemId}")
    public DeviceItem getByItemId(@PathVariable Long deviceItemId){
        return stockService.findStockByItemId(deviceItemId);
    }

    @GetMapping("/device/{deviceDefinitionId}")
    public Iterable<DeviceItem> getAllDevicesByDefinitionId(@PathVariable Long deviceDefinitionId){
        return stockService.findStockByDeviceDefinition(deviceDefinitionId);
    }

    @PutMapping("/hire/{deviceItemId}/user/{userId}/from/{deviceHireFrom}/to/{deviceHireTo}")
    public ResponseEntity putHireDevice(@PathVariable Long deviceItemId,
                                        @PathVariable Long userId,
                                        @PathVariable String deviceHireFrom,
                                        @PathVariable String deviceHireTo) throws HireDateParseException{
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime hireDateFrom;
        LocalDateTime hireDateTo;
        try {
            hireDateFrom = LocalDate.parse(deviceHireFrom,formatter).atStartOfDay();
            hireDateTo = LocalDate.parse(deviceHireTo,formatter).atStartOfDay();
        }catch (DateTimeParseException e) {
            throw new HireDateParseException("" + e.getParsedString());
        }

        stockService.hireItem(deviceItemId,hireDateFrom,hireDateTo, userId);

        return ResponseEntity.accepted().body("Device Hired");
    }
}
