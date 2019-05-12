package pl.dopierala.wirepickapi.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.dopierala.wirepickapi.Utils;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;
import pl.dopierala.wirepickapi.model.device.DeviceItem;
import pl.dopierala.wirepickapi.repositories.devices.DevicesDefinitionRepository;
import pl.dopierala.wirepickapi.repositories.devices.StockRepository;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    StockRepository stockRepository;
    DevicesDefinitionRepository devicesDefinitionRepository;

    @Autowired
    public WebMvcConfig(StockRepository stockRepository, DevicesDefinitionRepository devicesDefinitionRepository) {
        this.stockRepository = stockRepository;
        this.devicesDefinitionRepository = devicesDefinitionRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void configSampleDevices() {

        if(Utils.getIterableSize(stockRepository.findAll())>4){
            return;
        }

        stockRepository.deleteAll();
        devicesDefinitionRepository.deleteAll();

        DeviceDefinition deviceDef1 = new DeviceDefinition();
        deviceDef1.setName("Zadajnik prądowy INMEL50");
        deviceDef1.setDescription("Opis zadajnika");

        DeviceItem dev1 = new DeviceItem(deviceDef1);
        dev1.setLocalization("Polka 1 rzad 1");
        DeviceItem dev2 = new DeviceItem(deviceDef1);
        dev2.setLocalization("Polka 1 rzad 2");
        DeviceItem dev3 = new DeviceItem(deviceDef1);
        dev3.setLocalization("Polka 1 rzad 3");

        DeviceDefinition deviceDef2 = new DeviceDefinition();
        deviceDef2.setName("Konwerter RS232 - RS485");
        deviceDef2.setDescription("Opis konwertera RS232 - RS485. Przydatne urządzenie.");

        DeviceItem dev4 = new DeviceItem(deviceDef2);
        dev4.setLocalization("Polka 2 rzad 1");
        DeviceItem dev5 = new DeviceItem(deviceDef2);
        dev5.setLocalization("Polka 2 rzad 2");

        DeviceDefinition deviceDef3 = new DeviceDefinition();
        deviceDef3.setName("Siemens interfejs Profibus/MPI CP5711");
        deviceDef3.setDescription("Opis interfejsu Profibus/MPI dla sterownikow PLC firmy Siemens.");

        DeviceItem dev6 = new DeviceItem(deviceDef3);
        dev6.setLocalization("Polka 3 rzad 1");

        devicesDefinitionRepository.save(deviceDef1);
        devicesDefinitionRepository.save(deviceDef2);
        devicesDefinitionRepository.save(deviceDef3);
        stockRepository.save(dev1);
        stockRepository.save(dev2);
        stockRepository.save(dev3);
        stockRepository.save(dev4);
        stockRepository.save(dev5);
        stockRepository.save(dev6);
    }
}
