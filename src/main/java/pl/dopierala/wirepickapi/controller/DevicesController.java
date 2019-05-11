package pl.dopierala.wirepickapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.service.DeviceService;

import java.util.Optional;

@RestController
@RequestMapping("/api/device")
public class DevicesController {
    private DeviceService deviceService;

    @Autowired
    public DevicesController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/all")
    public Iterable<DeviceDefinition> getAllDevices(){
        return deviceService.findAllDevices();
    }

    @GetMapping("/{deviceId}")
    public Optional<DeviceDefinition> getAllDevices(@PathVariable Long deviceId){
        return deviceService.findDeviceById(deviceId);
    }

    @PutMapping("")
    public long putNewDeviceDefinition(@RequestBody DeviceDefinition ){
        return 0L;
    }
}
