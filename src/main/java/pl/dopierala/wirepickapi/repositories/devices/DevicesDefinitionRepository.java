package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.device.DeviceDefinition;

@Repository
public interface DevicesDefinitionRepository extends CrudRepository<DeviceDefinition,Long> {
}
