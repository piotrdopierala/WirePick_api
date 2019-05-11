package pl.dopierala.wirepickapi.repositories.devices;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dopierala.wirepickapi.model.device.DeviceItem;

import java.time.LocalDateTime;

@Repository
public interface StockRepository extends CrudRepository<DeviceItem,Long> {
    Iterable<DeviceItem> findByDeviceDefinition_Id(Long id);


    //TODO finish test, sql from code below
    @Query(value="SELECT 1"
            ,nativeQuery = true)
    boolean isAvailable(Long itemId, LocalDateTime from, LocalDateTime to);

    //"SELECT count(1)=0 FROM wirepick.hire_event WHERE (@date_start <= hire_end AND @date_end >= hire_start)"
}
