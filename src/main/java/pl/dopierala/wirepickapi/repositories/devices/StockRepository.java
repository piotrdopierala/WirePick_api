package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

@Repository
public interface StockRepository extends CrudRepository<DeviceItem,Long> {
    Iterable<DeviceItem> findByDeviceDefinition_Id(Long id);
}
