package pl.dopierala.wirepickapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.repositories.devices.DevicesDefinitionRepository;

import java.util.Optional;

@Service
public class DeviceService {
    private DevicesDefinitionRepository  devicesDefinitionRepository;

    @Autowired
    public DeviceService(DevicesDefinitionRepository devicesDefinitionRepository) {
        this.devicesDefinitionRepository = devicesDefinitionRepository;
    }

    public Iterable<DeviceDefinition> findAllDevices(){
        return devicesDefinitionRepository.findAll();
    }

    public Optional<DeviceDefinition> findDeviceById(Long id){
        return devicesDefinitionRepository.findById(id);
    }

}
